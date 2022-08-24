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

import com.nccgroup.jwtreauth.ui.misc.InstantRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import java.time.Instant;
import java.util.List;

public class LogTable extends JTable {
    private final TableRowSorter<LogTableModel> sorter;

    private final TypeFilter typeFilter;
    private String regexFilter;

    public LogTable() {
        // it is now safe to create the table model and column model
        setModel(new LogTableModel());

        // use a custom formatter for the Time column
        var columnModel = getColumnModel();
        columnModel.getColumn(LogTableModel.TIME_COL).setCellRenderer(new InstantRenderer());

        // set each column to a custom width
        columnModel.getColumn(LogTableModel.TIME_COL).setPreferredWidth(160);
        columnModel.getColumn(LogTableModel.TIME_COL).setMaxWidth(160);
        columnModel.getColumn(LogTableModel.TYPE_COL).setPreferredWidth(60);
        columnModel.getColumn(LogTableModel.TYPE_COL).setMaxWidth(60);

        // create a new sorter
        sorter = new TableRowSorter<>((LogTableModel) getModel());
        sorter.setSortsOnUpdates(true);
        setRowSorter(sorter);

        sorter.setStringConverter(new TableStringConverter() {
            @Override
            public String toString(TableModel model, int row, int column) {
                var obj = model.getValueAt(row, column);
                if (column == LogTableModel.TIME_COL) {
                    return InstantRenderer.FORMAT.format((Instant) obj);
                }

                return obj.toString();
            }
        });

        typeFilter = new TypeFilter();
        regexFilter = "";

        updateFilter();

        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    public void setFilterIncludeError(boolean include) {
        this.typeFilter.setFilterInclude(LogLevel.Error, include);

        // trigger a sort to update the GUI
        SwingUtilities.invokeLater(this.sorter::sort);
    }

    public void setFilterIncludeInfo(boolean include) {
        this.typeFilter.setFilterInclude(LogLevel.Info, include);

        // trigger a sort to update the GUI
        SwingUtilities.invokeLater(this.sorter::sort);
    }

    public void setFilterIncludeDebug(boolean include) {
        this.typeFilter.setFilterInclude(LogLevel.Debug, include);

        // trigger a sort to update the GUI
        SwingUtilities.invokeLater(this.sorter::sort);
    }

    public void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;

        updateFilter();
    }

    private void updateFilter() {
        var newFilter = RowFilter.andFilter(List.of(
                this.typeFilter,
                RowFilter.regexFilter(regexFilter)
        ));

        SwingUtilities.invokeLater(() -> sorter.setRowFilter(newFilter));
    }
}
