// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth;

import burp.ITab;
import com.nccgroup.jwtreauth.ui.MainViewPane;

import javax.swing.*;
import java.awt.*;

public class MainViewController implements ITab {
    private final JTabbedPane tabbedPane;

    public MainViewController(JWTReauth jwtReauth) {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Main", new MainViewPane(jwtReauth));
        tabbedPane.addTab("Scope", jwtReauth.getScopeController().getScopePanel());
    }

    @Override
    public String getTabCaption() {
        return "JWT reauth";
    }

    @Override
    public Component getUiComponent() {
        return tabbedPane;
    }
}
