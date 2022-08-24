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

package com.nccgroup.jwtreauth.ui.scope;

import com.nccgroup.jwtreauth.ui.misc.AutoScrollPane;
import javax.validation.constraints.NotNull;

import javax.swing.*;
import java.awt.*;

public class ScopeViewPanel extends JPanel {
    private final ScopeTable scopeTable;

    ScopeViewPanel() {
        setLayout(new BorderLayout());

        this.scopeTable = new ScopeTable();

        var filterPanel = new ScopeFilterPanel(scopeTable);
        var scopePane = new AutoScrollPane(scopeTable);

        add(filterPanel, BorderLayout.PAGE_START);
        add(scopePane, BorderLayout.CENTER);
    }

    public @NotNull ScopeTable getScopeTable() {
        return this.scopeTable;
    }
}
