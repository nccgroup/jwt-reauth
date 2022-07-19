// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

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
