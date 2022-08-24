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

package com.nccgroup.jwtreauth.ui.logging;

import com.nccgroup.jwtreauth.ui.misc.KeyReleasedListener;
import com.nccgroup.jwtreauth.ui.misc.OnOffButton;

import javax.swing.*;
import java.awt.*;

class LogFilterPanel extends JPanel {
    public LogFilterPanel(LogTable logTable) {
        super(new GridLayout(0, 1));

        // create the label and set the font
        var titleLabel = new JLabel("Event Log");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        // create a new panel to hold the label
        var labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        labelPanel.add(titleLabel);

        // create the filter label
        var filterLabel = new JLabel("Filter: ");

        // create the buttons
        var errorButton = new OnOffButton("Error", TypeFilter.INCLUDE_ERROR_BY_DEFAULT);
        errorButton.addStateChangeListener(logTable::setFilterIncludeError);

        var infoButton = new OnOffButton("Info", TypeFilter.INCLUDE_INFO_BY_DEFAULT);
        infoButton.addStateChangeListener(logTable::setFilterIncludeInfo);

        var debugButton = new OnOffButton("Debug", TypeFilter.INCLUDE_DEBUG_BY_DEFAULT);
        debugButton.addStateChangeListener(logTable::setFilterIncludeDebug);

        // create a new panel to hold the filter button components
        var filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterButtonPanel.add(filterLabel);
        filterButtonPanel.add(errorButton);
        filterButtonPanel.add(infoButton);
        filterButtonPanel.add(debugButton);

        // create the search label
        var searchLabel = new JLabel("Search: ");

        // create a new text field and give it a keyRelease listener
        var filterSearchField = new JTextField();
        filterSearchField.addKeyListener(new KeyReleasedListener(
                e -> logTable.setRegexFilter(filterSearchField.getText())
        ));

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
        filterPanel.add(filterButtonPanel, BorderLayout.WEST);
        filterPanel.add(filterSearchPanel, BorderLayout.EAST);

        // pack everything into the panel
        add(labelPanel);
        add(filterPanel);
    }
}
