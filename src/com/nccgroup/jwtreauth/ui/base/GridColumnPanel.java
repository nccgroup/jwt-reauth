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

package com.nccgroup.jwtreauth.ui.base;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridColumnPanel extends JPanel {
    private final List<JPanel> columnGrids;

    public GridColumnPanel(String panelTitle, int leftColumns, boolean hasCentre, int rightColumns) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(panelTitle)
        ));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(
                        null,
                        panelTitle,
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        Font.getFont(Font.MONOSPACED)
                )
        ));

        // Create the vertical grids to hold the components
        columnGrids = new ArrayList<>();
        var layout = new GridLayout(0, 1, 5, 5);
        var border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        var centreColumns = (hasCentre ? 1 : 0);
        var totalColumns = leftColumns + centreColumns + rightColumns;
        for (int i = 0; i < totalColumns; i++) {
            var panel = new JPanel();
            panel.setLayout(layout);
            panel.setBorder(border);
            columnGrids.add(panel);
        }

        // pack the left columns
        var leftColumn = new JPanel(new BorderLayout());
        for (int i = 0; i < leftColumns; i++) {
            leftColumn.add(columnGrids.get(i), BorderLayout.EAST);

            var oldCol = leftColumn;
            leftColumn = new JPanel(new BorderLayout());
            leftColumn.add(oldCol, BorderLayout.WEST);
        }

        // pack right columns
        var rightColumn = new JPanel(new BorderLayout());
        for (int i = 0; i < rightColumns; i++) {
            rightColumn.add(columnGrids.get(leftColumns + centreColumns + i), BorderLayout.WEST);

            var oldCol = rightColumn;
            rightColumn = new JPanel(new BorderLayout());
            rightColumn.add(oldCol, BorderLayout.EAST);
        }

        // add the packed columns to the grid
        add(leftColumn, BorderLayout.WEST);
        if (hasCentre) add(columnGrids.get(leftColumns), BorderLayout.CENTER);
        add(rightColumn, BorderLayout.EAST);
    }

    /**
     * Add any number of components to the grid, any "missing" components, are defaulted
     * to an empty JPanel.
     *
     * @param components the list of components to add to the grid
     */
    public void addRow(Component... components) {
        for (int i = 0; i < columnGrids.size(); i++) {
            if (i < components.length) {
                this.columnGrids.get(i).add(components[i]);
            } else {
                this.columnGrids.get(i).add(new JPanel());
            }
        }
    }
}
