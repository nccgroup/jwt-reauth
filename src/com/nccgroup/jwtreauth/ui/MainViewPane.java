/*
Copyright 2022 NCC Group
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    https://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
