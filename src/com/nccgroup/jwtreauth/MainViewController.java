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
