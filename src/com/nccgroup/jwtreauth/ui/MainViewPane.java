// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui;

import com.nccgroup.jwtreauth.JWTReauth;

import javax.swing.*;
import java.awt.*;

public class MainViewPane extends JSplitPane {
    public MainViewPane(JWTReauth jwtReauth) {
        super(VERTICAL_SPLIT);

        var logViewPanel = jwtReauth.getLogController().getLogViewPanel();
        var settingsPanel = jwtReauth.getSettingsController().getSettingsViewPanel();
        var tokenListenerStatePanel = jwtReauth.getTokenListenerStatePanel();

        // add settings and state panels to the content panel
        var upperPanelInner = new JPanel();
        upperPanelInner.setLayout(new BoxLayout(upperPanelInner, BoxLayout.Y_AXIS));
        upperPanelInner.add(settingsPanel);
        upperPanelInner.add(tokenListenerStatePanel);

        // add another panel to pin those components to the top of the window
        var upperPanel = new JPanel(new BorderLayout());
        upperPanel.add(upperPanelInner, BorderLayout.NORTH);

        // create a split panel to hold all of the components
        setTopComponent(upperPanel);
        setBottomComponent(logViewPanel);

        // expand the log panel when the window resizes
        setResizeWeight(0.0);
    }
}
