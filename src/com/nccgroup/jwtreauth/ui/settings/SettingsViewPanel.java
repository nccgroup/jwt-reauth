// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.settings;

import com.nccgroup.jwtreauth.JWTReauth;
import com.nccgroup.jwtreauth.TokenListener;
import com.nccgroup.jwtreauth.ui.base.GridColumnPanel;
import com.nccgroup.jwtreauth.ui.logging.LogController;
import com.nccgroup.jwtreauth.ui.logging.LogLevel;
import com.nccgroup.jwtreauth.ui.logging.LogTableModel;
import com.nccgroup.jwtreauth.ui.misc.OnOffButton;
import com.nccgroup.jwtreauth.ui.misc.StatusLabel;
import com.nccgroup.jwtreauth.ui.state.TokenListenerStatePanel;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SettingsViewPanel extends GridColumnPanel {
    private final LogController logController;
    private final TokenListener tokenListener;
    private final TokenListenerStatePanel tokenListenerStatePanel;

    final HashMap<String, RowUpdateHandler> updateHandlers;

    public SettingsViewPanel(JWTReauth jwtReauth) {
        super("Settings", 3, false, 0);

        logController = jwtReauth.getLogController();
        tokenListener = jwtReauth.getTokenListener();
        tokenListenerStatePanel = jwtReauth.getTokenListenerStatePanel();

        updateHandlers = new HashMap<>();

        addComponents();
    }

    private void addComponents() {
        // Create the auth URL row
        var authRow = new RowBuilder(this, "authURL")
                .setLabelText("Authorization URL: ")
                .setFieldText(TokenListener.DEFAULT_AUTH_URL)
                .addStatusLabel()
                .setKeyReleasedStatusHandler((field, status) -> {
                    var url = field.getText();
                    final URL authUrl;

                    if (url.isBlank()) return;

                    try {
                        authUrl = new URL(url);
                    } catch (MalformedURLException e) {
                        status.setStatus(StatusLabel.Status.ERROR);

                        logController.error(
                                "Failed to set Authorization URL: %s - %s",
                                url, e
                        );

                        return;
                    }

                    status.setStatus(StatusLabel.Status.OK);
                    tokenListener.setAuthorizeURL(authUrl);

                    logController.debug("Set auth URL = %s", authUrl);
                })
                .build();
        addRow(authRow);

        // Create the auth request delay row
        var delayLabel = new JLabel("Authorization Request Delay (seconds): ");
        var delaySpinnerModel = new SpinnerNumberModel(TokenListener.DEFAULT_AUTH_REQ_DELAY, 5, null, 5);
        var delaySpinner = new JSpinner(delaySpinnerModel);
        delaySpinner.addChangeListener(e -> {
            var delay = delaySpinnerModel.getNumber().longValue();

            logController.debug("Set delay = %d", delay);

            tokenListenerStatePanel.setTokenRefreshDuration(Duration.ofSeconds(delay));
        });
        updateHandlers.put("delay", delaySpinnerModel::setValue);
        addRow(delayLabel, delaySpinner);

        // Create the header name row
        var headerNameRow = new RowBuilder(this, "headerName")
                .setLabelText("Header name: ")
                .setFieldText(TokenListener.DEFAULT_HEADER_NAME)
                .addStatusLabel()
                .setKeyReleasedStatusHandler((field, status) -> {
                    var headerName = field.getText();

                    if (headerName.isBlank()) return;

                    if (headerName.contains(":")) {
                        status.setStatus(StatusLabel.Status.ERROR);

                        logController.error(
                                "Failed to set headerName, header cannot contain \":\""
                        );
                    } else {
                        status.setStatus(StatusLabel.Status.OK);
                        tokenListener.setHeaderName(headerName);

                        logController.debug(
                                "Set header name = %s", headerName
                        );
                    }
                })
                .build();
        addRow(headerNameRow);

        // Create the header value prefix row
        var headerValuePrefixRow = new RowBuilder(this, "headerValuePrefix")
                .setLabelText("Header value prefix: ")
                .setFieldText(TokenListener.DEFAULT_HEADER_VALUE_PREFIX)
                .setKeyReleasedHandler((field) -> {
                    var prefix = field.getText();

                    // log an info message if the prefix doesn't end with a space as this is likely a mistake
                    if (!prefix.endsWith(" ")) {
                        logController.info(
                                "Header value prefix does not end with a space, this might be a mistake."
                        );
                    }

                    tokenListener.setHeaderValuePrefix(prefix);

                    logController.debug(
                            "Set header value prefix = %s", prefix
                    );
                })
                .build();
        addRow(headerValuePrefixRow);

        // Create the token regex row
        var tokenRegexRow = new RowBuilder(this, "tokenRegex")
                .setLabelText("Token regex: ")
                .setFieldText(TokenListener.DEFAULT_TOKEN_REGEX)
                .addStatusLabel()
                .setKeyReleasedStatusHandler((field, status) -> {
                    var regex = field.getText();
                    final Pattern tokenPattern;

                    try {
                        tokenPattern = Pattern.compile(regex);
                    } catch (PatternSyntaxException e) {
                        status.setStatus(StatusLabel.Status.ERROR);

                        logController.error(
                                "Failed to set new Token Regex: %s - %s",
                                regex, e
                        );

                        return;
                    }

                    status.setStatus(StatusLabel.Status.OK);
                    tokenListener.setTokenPattern(tokenPattern);

                    logController.debug(
                            "Set token regex = %s", tokenPattern
                    );
                })
                .build();
        addRow(tokenRegexRow);

        // create the "listening" row
        var listeningLabel = new JLabel("Listening: ");
        var listeningButton = new OnOffButton("listening", "not listening", TokenListener.DEFAULT_IS_LISTENING);
        listeningButton.addStateChangeListener(tokenListener::setIsListening);
        updateHandlers.put(
                "isListening",
                newData -> {
                    if (newData instanceof Boolean) {
                        SwingUtilities.invokeLater(() -> {
                            listeningButton.setState((Boolean) newData);

                            logController.debug(
                                    "Set listening = %b", newData
                            );
                        });
                    }
                }
        );
        addRow(listeningLabel, listeningButton);

        // create the log level row
        var logLevelLabel = new JLabel("Log Level: ");
        var logLevelBox = new JComboBox<>(LogLevel.values());
        logLevelBox.setFont(Font.decode("MONOSPACED"));
        logLevelBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        logLevelBox.setSelectedItem(LogTableModel.DEFAULT_LOG_LEVEL);
        logLevelBox.addActionListener(_event -> SwingUtilities.invokeLater(() -> {
            logController.setLogLevel((LogLevel) logLevelBox.getSelectedItem());

            logController.debug(
                    "Set log level = %s", logLevelBox.getSelectedItem()
            );
        }));
        updateHandlers.put("logLevel", logLevelBox::setSelectedItem);
        addRow(logLevelLabel, logLevelBox);

        // Create the max log length row
        var maxLogLengthLabel = new JLabel("Max number of log entries: ");
        var maxLogLengthSpinnerModel = new SpinnerNumberModel(LogTableModel.DEFAULT_MAX_LOG_LENGTH, 0, null, 1000);
        var maxLogLengthSpinner = new JSpinner(maxLogLengthSpinnerModel);
        maxLogLengthSpinner.addChangeListener(e -> {
            var maxLogLength = maxLogLengthSpinnerModel.getNumber().intValue();

            logController.debug("Set maxLogLength = %d", maxLogLength);

            logController.setMaxLogLength(maxLogLength);
        });
        updateHandlers.put("maxLogLength", maxLogLengthSpinnerModel::setValue);
        addRow(maxLogLengthLabel, maxLogLengthSpinner);

    }

    void updateRow(String rowID, Object newData) {
        updateHandlers.get(rowID).update(newData);
    }
}
