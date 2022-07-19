// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth;

import burp.*;
import com.nccgroup.jwtreauth.ui.logging.LogController;
import com.nccgroup.jwtreauth.ui.scope.ScopeController;
import com.nccgroup.jwtreauth.ui.settings.SettingsController;
import com.nccgroup.jwtreauth.ui.state.TokenListenerStatePanel;
import com.nccgroup.jwtreauth.utils.UrlComparison;
import javax.validation.constraints.NotNull;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class TokenListener implements IHttpListener, IExtensionStateListener {
    public static final String DEFAULT_AUTH_URL = "https://domain.sld.tld:443/path";
    public static final int DEFAULT_AUTH_REQ_DELAY = 300;
    public static final String DEFAULT_HEADER_NAME = "Authorization";
    public static final String DEFAULT_HEADER_VALUE_PREFIX = "Bearer ";
    public static final String DEFAULT_TOKEN_REGEX = "\"access_token\":\\s?\"([^\"]*)\"";
    public static final String DEFAULT_TOKEN_MISSING = "<no token found yet>";
    public static final String DEFAULT_HEADER_MISSING = "<no header made yet>";
    public static final boolean DEFAULT_IS_LISTENING = false;

    private final AtomicInteger lastRefreshStamp = new AtomicInteger(0);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final IBurpExtenderCallbacks callbacks;
    private final IExtensionHelpers helpers;
    private final LogController logController;
    private final ScopeController scopeController;

    private final TokenListenerStatePanel tokenListenerStatePanel;

    private SettingsController settingsController;

    private URL authorizeURL;
    private String headerName;
    private String headerValuePrefix;
    private Pattern tokenPattern;
    private boolean isListening;
    private boolean tokenSetManually;
    private Optional<String> token;
    private Optional<String> header;
    private Optional<IHttpRequestResponse> authorizeRequest;

    public TokenListener(JWTReauth jwtReauth) {
        callbacks = jwtReauth.getCallbacks();
        helpers = callbacks.getHelpers();
        logController = jwtReauth.getLogController();
        scopeController = jwtReauth.getScopeController();

        this.initDefaults();

        tokenListenerStatePanel = new TokenListenerStatePanel(this);
    }

    private void initDefaults() {
        try {
            authorizeURL = new URL(DEFAULT_AUTH_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        isListening = DEFAULT_IS_LISTENING;
        headerName = DEFAULT_HEADER_NAME;
        headerValuePrefix = DEFAULT_HEADER_VALUE_PREFIX;

        tokenPattern = Pattern.compile(DEFAULT_TOKEN_REGEX);
        token = Optional.empty();

        authorizeRequest = Optional.empty();
    }

    public TokenListenerStatePanel getTokenListenerStatePanel() {
        return tokenListenerStatePanel;
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse currentRequest) {
        // if we aren't listening, return immediately
        if (!isListening) return;

        var requestInfo = helpers.analyzeRequest(currentRequest);
        var URLIsAuthorizeURL = UrlComparison.compareEqual(requestInfo.getUrl(), authorizeURL);

        if (messageIsRequest && !URLIsAuthorizeURL) {
            // if we have a valid token, the request URL is not equal to the authentication URL
            // and the URL is in scope, then we should attach the headers

            if (token.isPresent() && scopeController.inScope(requestInfo.getUrl())) {
                logController.debug(
                        "URL: %s matches scope, adding header.%n",
                        requestInfo.getUrl()
                );

                var updatedRequest = replaceHeaders(currentRequest);

                currentRequest.setRequest(updatedRequest);
            } else {
                logController.debug(
                        "Request URL: %s does not match scope, ignoring.%n",
                        requestInfo.getUrl()
                );
            }
        } else if (!messageIsRequest && URLIsAuthorizeURL) {
            logController.debug(
                    "Request URL: %s matches authorization URL: %s, attempting to parse token from response.%n",
                    requestInfo.getUrl(), authorizeURL
            );
            processAuthResponse(currentRequest);
        }
    }

    /**
     * Processes responses from the authentication URL to extract the token.
     *
     * @param currentRequest the current request to the auth URL
     */
    public void processAuthResponse(@NotNull IHttpRequestResponse currentRequest) {
        processAuthResponse(currentRequest, false);
    }

    /**
     * Processes responses from the authentication URL to extract the token.
     *
     * @param currentRequest the current request to the auth URL
     */
    public void processAuthResponse(@NotNull IHttpRequestResponse currentRequest, boolean fromContextMenu) {
        // if the current token is being set from the context menu then it overrides the manual one
        if (fromContextMenu) {
            tokenSetManually = false;
        }

        // if the current token was set manually don't attempt to process a new one
        if (tokenSetManually) {
            logController.debug("Token set manually, ignoring auth response.");
            return;
        }

        // create a matcher over the entire response
        var m = tokenPattern.matcher(
                helpers.bytesToString(currentRequest.getResponse())
        );

        if (m.find()) {
            var token = m.group(1);
            updateToken(token);

            logController.info(
                    "Parsed token \"%s\" from response to authorization URL.", token
            );
        } else {
            logController.info("Failed to parse token from response to authorization URL.");
        }
    }

    /**
     * Schedules an attempt to refresh the token.
     * <p>
     * we increment stamp atomically and get the result, if the stamp hasn't
     * changed by the time we are called two seconds later, then we refresh the token.
     */
    public void scheduleTokenRefresh() {
        logController.debug("Token refresh scheduled.");
        var stamp = this.lastRefreshStamp.incrementAndGet();

        executor.schedule(
                () -> {
                    int currStamp = this.lastRefreshStamp.get();
                    if (stamp == currStamp) {
                        logController.debug("Stamps match - refreshing token.");
                        this.refreshToken();
                    } else {
                        logController.debug("Stamps don't match - ignoring refresh.");
                    }
                },
                2,
                TimeUnit.SECONDS);
    }

    /**
     * Attempt the refresh the current token, either by using a request sent to
     * the extension, or by creating a simple HTTP GET request to the auth URL.
     */
    private void refreshToken() {
        IHttpRequestResponse resp = null;
        try {
            if (this.authorizeRequest.isPresent()) {
                var req = this.authorizeRequest.get();

                // the listener will automatically pick up on the response and parse the token
                resp = callbacks.makeHttpRequest(
                        req.getHttpService(), req.getRequest()
                );
            } else {
                // don't make requests to the default URL
                if (this.authorizeURL.toString().equals(TokenListener.DEFAULT_AUTH_URL)) return;

                var service = helpers.buildHttpService(
                        this.authorizeURL.getHost(),
                        this.authorizeURL.getPort(),
                        this.authorizeURL.getProtocol()
                );

                var request = helpers.buildHttpRequest(this.authorizeURL);

                // the listener will automatically pick up on the response and parse the token
                resp = callbacks.makeHttpRequest(service, request);
            }
        } catch (RuntimeException e) {
            SwingUtilities.invokeLater(
                    () -> logController.error(
                            "Caught exception while refreshing token: %s", e
                    )
            );
        }

        // if we are already listening then it will get processed with the rest of the tokens
        if (resp != null && !isListening) {
            processAuthResponse(resp);
        }
    }

    /**
     * Helper method to replace the authentication headers on a given request.
     * It gets the headers from the burpsuite helpers then filters out any which
     * start with the same header prefix as our authentication header.
     * Then finally adds out auth header back in and rebuilds the request.
     *
     * @param currentRequest the request to replace the headers on
     * @return the new request with its headers replaced
     */
    private byte[] replaceHeaders(IHttpRequestResponse currentRequest) {
        var requestInfo = helpers.analyzeRequest(currentRequest);

        // Remove the old auth header and add a new one with the correct token
        var headers = (ArrayList<String>) requestInfo.getHeaders();
        headers.removeIf(header -> header.startsWith(this.makeHeaderPrefix()));
        headers.add(this.makeHeader());

        // Replace the current request with a new request with the updated headers
        return helpers.buildHttpMessage(headers,
                Arrays.copyOfRange(currentRequest.getRequest(),
                        requestInfo.getBodyOffset(),
                        currentRequest.getRequest().length));
    }

    /**
     * Helper method to invalidate the cached authorization request.
     */
    private void invalidateCachedRequest() {
        // stop listening whenever we invalidate something
        setIsListening(false);

        // invalidate the request
        this.authorizeRequest = Optional.empty();

        // invalidate the token
        this.invalidateCachedToken();
    }

    /**
     * Helper method to invalidate the cached token.
     * <p>
     * Note: can update the GUI
     */
    private void invalidateCachedToken() {
        // stop listening whenever we invalidate the token
        setIsListening(false);

        this.token = Optional.empty();

        this.invalidateCachedHeader(false);

        // update the GUI
        tokenListenerStatePanel.updateToken(DEFAULT_TOKEN_MISSING, false);
    }

    /**
     * Helper method to invalidate the cached header.
     * <p>
     * Note: can update the GUI
     */
    private void invalidateCachedHeader(boolean setIsListening) {
        if (setIsListening) setIsListening(false);

        // invalidate the header
        this.header = Optional.empty();

        // update the GUI
        tokenListenerStatePanel.setHeaderFieldText(DEFAULT_HEADER_MISSING);
    }

    /**
     * Helper method to handle all of the logic for when the token is updated.
     * <p>
     * Note: can update the GUI
     *
     * @param newToken the token value that we just parsed from a request
     */
    private void updateToken(@NotNull String newToken) {
        this.invalidateCachedHeader(false);

        this.token = Optional.of(newToken);

        // update the GUI with the new token
        tokenListenerStatePanel.updateToken(newToken, false);
    }

    /**
     * Helper method to handle all of the logic for when the token is set manually.
     * <p>
     * Note: can update the GUI
     *
     * @param newToken the token set by the user
     */
    public void setTokenManual(@NotNull String newToken) {
        this.invalidateCachedHeader(false);

        this.token = Optional.of(newToken);
        this.tokenSetManually = true;

        logController.debug("Token set manually: token = \"%s\"", this.token);

        // update the GUI with the new token
        tokenListenerStatePanel.updateToken(newToken, true);
    }

    /**
     * Helper method to have a uniform way to create the header prefix.
     *
     * @return the prefix used to create the header value
     */
    @NotNull
    private String makeHeaderPrefix() {
        // there is no point caching this, it's very cheap to update and doesn't have a GUI component
        return headerName + ": " + headerValuePrefix;
    }

    /**
     * Helper method to handle caching the header value.
     * <p>
     * Note: can update the GUI
     *
     * @return the most recently used header, creating it if it doesn't exist.
     */
    @NotNull
    private String makeHeader() {
        if (this.header.isEmpty()) {
            this.header = Optional.of(this.makeHeaderPrefix() + token.orElse("<no token>"));

            // update the GUI with the created header
            tokenListenerStatePanel.setHeaderFieldText(this.header.get());
        }

        return this.header.get();
    }

    /**
     * Setter method for the authorizeURL property.
     * <p>
     * Note: can update the GUI
     * attempts to request a token from the authorization URL
     */
    public void setAuthorizeURL(@NotNull URL newAuthorizeURL) {
        if (!UrlComparison.compareEqual(authorizeURL, newAuthorizeURL)) {
            this.invalidateCachedRequest();

            this.authorizeURL = newAuthorizeURL;

            logController.debug(
                    "Set new Authorization URL: %s", newAuthorizeURL
            );

            this.scheduleTokenRefresh();
        }
    }

    /**
     * Setter method for the authorizeRequest property.
     * <p>
     * Note: always updates the GUI
     * attempts to request a token from the authorization URL
     */
    public void setAuthorizeRequest(IHttpRequestResponse authorizeRequest) {
        // note: we always invalidate the cache here because the request
        //       may have the same URL but different parameters.
        invalidateCachedRequest();

        authorizeURL = helpers.analyzeRequest(authorizeRequest).getUrl();
        settingsController.updateRow("authURL", this.authorizeURL.toString());

        // setting the authentication request overrides a manual token
        this.tokenSetManually = false;
        this.authorizeRequest = Optional.of(authorizeRequest);

        logController.debug("Set new Authorization Requst.");

        this.scheduleTokenRefresh();
    }

    /**
     * Setter method for the headerName property.
     * <p>
     * Note: can update the GUI
     */
    public void setHeaderName(String newHeaderName) {
        if (!this.headerName.equals(newHeaderName)) {
            this.invalidateCachedHeader(true);

            this.headerName = newHeaderName;

            logController.debug(
                    "Set new Header Name: %s", newHeaderName
            );
        }
    }

    /**
     * Setter method for the headerValuePrefix property.
     * <p>
     * Note: can update the GUI
     */
    public void setHeaderValuePrefix(String newHeaderValuePrefix) {
        if (!this.headerValuePrefix.equals(newHeaderValuePrefix)) {
            this.invalidateCachedHeader(true);

            this.headerValuePrefix = newHeaderValuePrefix;

            logController.debug(
                    "Set new Header Value Prefix: %s", newHeaderValuePrefix
            );
        }
    }

    /**
     * Setter method for the tokenPattern property.
     * <p>
     * Note: can update the GUI
     */
    public void setTokenPattern(Pattern newTokenPattern) {
        if (!this.tokenPattern.equals(newTokenPattern)) {
            this.invalidateCachedToken();

            this.tokenPattern = newTokenPattern;

            logController.debug(
                    "Set new Token Regex: %s", newTokenPattern
            );
        }
    }

    /**
     * Setter method for the isListening property.
     * If isListening is false the plugin should not modify any requests,
     * and it should invalidate any tokens.
     * <p>
     * If isListening is true the plugin should try to get a new token,
     * and it should start checking incoming requests so it can add headers
     * if they are in scope.
     * <p>
     * Note: can update the GUI
     */
    public void setIsListening(boolean isListening) {
        // setting it to the same value is a nop
        if (this.isListening == isListening) return;

        this.isListening = isListening;
        settingsController.updateRow("isListening", isListening);

        if (this.isListening) {
            // if we are starting to listen again, attempt to fetch a fresh token
            this.scheduleTokenRefresh();
        } else {
            tokenListenerStatePanel.stopTimer();
            invalidateCachedToken();
        }
    }

    /**
     * Getter method for the isListening property.
     *
     * @return the current value of isListening.
     */
    public boolean isListening() {
        return this.isListening;
    }

    /**
     * Getter method for the tokenSetManually property.
     *
     * @return the current value of tokenSetManually.
     */
    public boolean isTokenSetManually() {
        return this.tokenSetManually;
    }

    /**
     * This method is necessary because the settings controller depends on this class.
     * But this class only requires the settingsController once the plugin is started.
     */
    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    @Override
    public void extensionUnloaded() {
        // stop submission of new tasks
        executor.shutdown();

        // Cancel currently executing tasks
        executor.shutdownNow();
    }
}
