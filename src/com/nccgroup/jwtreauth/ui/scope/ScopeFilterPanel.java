// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.scope;

import com.nccgroup.jwtreauth.ui.misc.KeyReleasedListener;

import javax.swing.*;
import java.awt.*;

public class ScopeFilterPanel extends JPanel {
    ScopeFilterPanel(ScopeTable scopeTable) {
        super(new GridLayout(0, 1));

        // create the label and set the font
        var titleLabel = new JLabel("Scope");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        // create a new panel to hold the label
        var labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        labelPanel.add(titleLabel);

        // create the filter label
        var filterLabel = new JLabel("Filter: ");

        // create the option picker
        var filterOptions = new JComboBox<>(ScopeFilter.values());
        filterOptions.addActionListener(e -> scopeTable.setFilterScope((ScopeFilter) filterOptions.getSelectedItem()));

        // create a new panel to hold the filter button components
        var filterOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterOptionsPanel.add(filterLabel);
        filterOptionsPanel.add(filterOptions);

        // create the search label
        var searchLabel = new JLabel("Search: ");

        // create a new text field and give it a keyRelease listener
        var filterSearchField = new JTextField();
        filterSearchField.addKeyListener(new KeyReleasedListener(
                e -> scopeTable.setFilterSearch(filterSearchField.getText()))
        );

        // create a new panel to hold the search box
        var filterSearchPanel = new JPanel(new GridBagLayout());
        filterSearchPanel.setPreferredSize(new Dimension(250, 0));

        var gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        filterSearchPanel.add(searchLabel, gbc);

        gbc.weightx = 8.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 5);
        filterSearchPanel.add(filterSearchField, gbc);

        // create a new panel to hold all the filter components
        var filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(filterOptionsPanel, BorderLayout.WEST);
        filterPanel.add(filterSearchPanel, BorderLayout.EAST);

        add(labelPanel);
        add(filterPanel);
    }
}
