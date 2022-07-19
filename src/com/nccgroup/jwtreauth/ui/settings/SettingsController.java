// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.settings;

import com.nccgroup.jwtreauth.JWTReauth;

public class SettingsController {
    private final SettingsViewPanel settingsViewPanel;

    public SettingsController(JWTReauth jwtReauth) {
        settingsViewPanel = new SettingsViewPanel(jwtReauth);
    }

    public SettingsViewPanel getSettingsViewPanel() {
        return settingsViewPanel;
    }

    public void updateRow(String rowID, Object newValue) {
        settingsViewPanel.updateRow(rowID, newValue);
    }
}
