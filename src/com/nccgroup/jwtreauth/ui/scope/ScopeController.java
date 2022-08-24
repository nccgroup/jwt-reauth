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

import java.net.URL;
import javax.validation.constraints.NotNull;

public class ScopeController {
    private final ScopeTable scopeTable;
    private final ScopeTableModel scopeTableModel;
    private final ScopeViewPanel scopeViewPanel;

    public ScopeController() {
        scopeViewPanel = new ScopeViewPanel();
        scopeTable = scopeViewPanel.getScopeTable();
        scopeTableModel = (ScopeTableModel) scopeTable.getModel();
    }

    public ScopeTable getScopeTable() {
        return scopeTable;
    }

    public ScopeViewPanel getScopePanel() {
        return scopeViewPanel;
    }

    public void addToScope(@NotNull URL url) {
        scopeTable.addRow(url.toString());
    }

    public boolean inScope(@NotNull URL url) {
        return scopeTableModel.inScope(url.toString());
    }

    public boolean contains(@NotNull URL url) {
        return scopeTableModel.contains(url.toString());
    }
}
