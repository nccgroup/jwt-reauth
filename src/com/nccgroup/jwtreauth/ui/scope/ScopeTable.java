// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.scope;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableRowSorter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
    Note: In this class I use Active column and "in scope" column interchangeably.
          They both refer to the first column in the table.
 */
public class ScopeTable extends JTable {
    private final TableRowSorter<ScopeTableModel> sorter;

    private final InScopeFilter scopeFilter;
    private String filterSearch;

    ScopeTable() {
        setModel(new ScopeTableModel());

        // fix the size of the "active" / "in scope" column
        final var columnModel = getColumnModel();
        columnModel.getColumn(ScopeTableModel.IN_SCOPE_COL).setPreferredWidth(50);
        columnModel.getColumn(ScopeTableModel.IN_SCOPE_COL).setMaxWidth(50);
        columnModel.getColumn(ScopeTableModel.IS_PREFIX_COL).setPreferredWidth(50);
        columnModel.getColumn(ScopeTableModel.IS_PREFIX_COL).setMaxWidth(50);

        sorter = new TableRowSorter<>((ScopeTableModel) getModel());
        sorter.setSortsOnUpdates(true);
        setRowSorter(sorter);

        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        addPopupMenu();
        addEmptyRow();

        scopeFilter = new InScopeFilter();
        filterSearch = "";

        // create the filter
        updateFilter();
    }

    private void addPopupMenu() {
        var removeItem = new JMenuItem("Remove");

        removeItem.addActionListener(e -> {
            // sorting rows biggest to smallest means that we don't move
            // any of the rows we are going to remove in the future
            // while we are removing previous rows
            var biggestToSmallestRows = Arrays.stream(getSelectedRows()).boxed().toArray(Integer[]::new);
            Arrays.sort(biggestToSmallestRows, Collections.reverseOrder());

            for (var row : biggestToSmallestRows) {
                ((ScopeTableModel) getModel()).removeRow(row);
            }

            ensureEmptyLastRow();
        });

        var popupMenu = new JPopupMenu();
        popupMenu.add(removeItem);

        setComponentPopupMenu(popupMenu);
    }

    /*
     * Override the base editingStopped method so that we can check whether the cell that
     * was just edited, was:
     * a. the last cell in the URL column
     * b. filled with a non-blank value
     *
     * We do this by getting the editing row and column, then letting the editor finish up,
     * and finally we can check the value in the cell. Adding a new empty row if a non-empty value was entered.
     *
     * This ordering of events is important for the following reasons:
     * 1. The editing row and column are both -1 once editingStopped(), is called.
     * 2. The cell's value will be blank before we call editingStopped().
     */
    @Override
    public void editingStopped(ChangeEvent e) {
        // NOTE: ngl chief this is almost certainly the wrong way to do this, but it works.

        // grab the row and column before they become invalid
        var row = this.getEditingRow();
        var col = this.getEditingColumn();

        // allow the editor to finish up
        super.editingStopped(e);

        // if we edited the URL and it was the last row
        if (col == ScopeTableModel.URL_COL && row == getModel().getRowCount() - 1) {
            // check the contents of the edited cell to see if they are blank
            var cellUrl = (String) getModel().getValueAt(row, ScopeTableModel.URL_COL);

            if (!cellUrl.isBlank()) {
                // if the new URL is not blank then add a new empty row
                addEmptyRow();
            }
        }
    }

    /**
     * helper method to ensure that the last row in the scope table is blank
     * so the user can add a scope to it by typing
     */
    private synchronized void ensureEmptyLastRow() {
        var rows = getModel().getRowCount();

        if (rows == 0) {
            addEmptyRow();
        } else {
            var lastUrl = (String) getModel().getValueAt(rows - 1, ScopeTableModel.URL_COL);
            if (!lastUrl.isBlank()) {
                addEmptyRow();
            }
        }
    }

    synchronized void addEmptyRow() {
        ((ScopeTableModel) getModel()).addRow(false, false, "");
    }

    synchronized void addRow(String url) {
        var model = (ScopeTableModel) getModel();
        var rows = model.getRowCount();

        if (rows == 0) {
            // in case the user deletes everything...
            model.addRow(true, false, url);
        } else {
            var lastUrl = (String) model.getValueAt(rows - 1, ScopeTableModel.URL_COL);

            // if the last row is blank fill in that row
            if (lastUrl.isBlank()) {
                model.setRow(rows - 1, true, false, url);
            } else {
                // otherwise just add a new row
                model.addRow(true, false, url);
            }
        }

        // always keep an empty row free at the end
        addEmptyRow();
    }

    void setFilterSearch(String filterSearch) {
        this.filterSearch = filterSearch;

        updateFilter();
    }

    void setFilterScope(ScopeFilter filter) {
        this.scopeFilter.setFilter(filter);

        this.sorter.sort();
    }

    private void updateFilter() {
        SwingUtilities.invokeLater(() -> sorter.setRowFilter(
                RowFilter.andFilter(List.of(
                        scopeFilter,
                        RowFilter.regexFilter(this.filterSearch, ScopeTableModel.URL_COL)
                ))
        ));
    }
}
