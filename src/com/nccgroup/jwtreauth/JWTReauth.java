// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import com.nccgroup.jwtreauth.ui.logging.LogController;
import com.nccgroup.jwtreauth.ui.scope.ScopeController;
import com.nccgroup.jwtreauth.ui.settings.SettingsController;
import com.nccgroup.jwtreauth.ui.state.TokenListenerStatePanel;

import javax.swing.*;

public class JWTReauth implements IBurpExtender {
    private static final String VERSION = "1.1.1";

    private IBurpExtenderCallbacks callbacks;
    private LogController logController;
    private ScopeController scopeController;
    private SettingsController settingsController;
    private TokenListenerStatePanel tokenListenerStatePanel;
    private TokenListener tokenListener;
    private MainViewController mainViewController;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;

        logController = new LogController();
        scopeController = new ScopeController();
        tokenListener = new TokenListener(this);
        tokenListenerStatePanel = tokenListener.getTokenListenerStatePanel();
        settingsController = new SettingsController(this);
        tokenListener.setSettingsController(settingsController);
        mainViewController = new MainViewController(this);

        callbacks.setExtensionName("JWT re-auth");
        callbacks.registerHttpListener(tokenListener);
        callbacks.registerContextMenuFactory(new JWTReauthContextMenuFactory(this));
        callbacks.registerExtensionStateListener(tokenListener);
        callbacks.registerExtensionStateListener(tokenListenerStatePanel);

        SwingUtilities.invokeLater(() -> {
            callbacks.addSuiteTab(mainViewController);
            callbacks.printOutput("Loaded JWT re-auth. v" + VERSION);
        });
    }

    public IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }

    public LogController getLogController() {
        return logController;
    }

    public ScopeController getScopeController() {
        return scopeController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public TokenListener getTokenListener() {
        return tokenListener;
    }

    public TokenListenerStatePanel getTokenListenerStatePanel() {
        return tokenListenerStatePanel;
    }
}
