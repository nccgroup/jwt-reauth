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

import javax.validation.constraints.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implement a custom model for the table.
 */
class ScopeTableModel extends AbstractTableModel {
    public static final int IN_SCOPE_COL = 0;
    public static final int IS_PREFIX_COL = 1;
    public static final int URL_COL = 2;

    private final static List<String> columnNames = List.of("Active", "Prefix", "URL");
    private final static List<Class<?>> columnClasses = List.of(Boolean.class, Boolean.class, String.class);

    private final List<List<Object>> data;
    private final List<Object> inScopeCol;
    private final List<Object> isPrefixCol;
    private final List<Object> URLCol;

    ScopeTableModel() {
        inScopeCol = new ArrayList<>();
        isPrefixCol = new ArrayList<>();
        URLCol = new ArrayList<>();
        data = List.of(inScopeCol, isPrefixCol, URLCol);
    }

    synchronized void addRow(boolean inScope, boolean isPrefix, String url) {
        var index = getRowCount();

        inScopeCol.add(inScope);
        isPrefixCol.add(isPrefix);
        URLCol.add(url);

        fireTableRowsInserted(index, index);
    }

    synchronized void removeRow(int rowIndex) {
        this.inScopeCol.remove(rowIndex);
        this.isPrefixCol.remove(rowIndex);
        this.URLCol.remove(rowIndex);

        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    synchronized void setRow(int rowIndex, boolean inScope, boolean isPrefix, String url) {
        this.inScopeCol.set(rowIndex, inScope);
        this.isPrefixCol.set(rowIndex, isPrefix);
        this.URLCol.set(rowIndex, url);

        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    synchronized boolean inScope(@NotNull String url) {
        return IntStream.iterate(0, i -> i + 1)
                .limit(getRowCount())
                .filter(i -> (Boolean) inScopeCol.get(i))
                .filter(i -> (Boolean) isPrefixCol.get(i)
                        ? url.startsWith((String) URLCol.get(i))
                        : url.equals(URLCol.get(i)))
                .findAny()
                .isPresent();
    }

    synchronized boolean contains(String url) {
        return URLCol.stream().anyMatch(url::equals);
    }

    @Override
    public int getColumnCount() {
        return data.size();
    }

    @Override
    public int getRowCount() {
        return inScopeCol.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(columnIndex).get(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.get(columnIndex).set(rowIndex, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses.get(columnIndex);
    }
}
