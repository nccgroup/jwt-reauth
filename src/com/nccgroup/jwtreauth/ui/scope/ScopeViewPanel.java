// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

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
