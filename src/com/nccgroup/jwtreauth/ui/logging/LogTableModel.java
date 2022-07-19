// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.logging;

import javax.validation.constraints.NotNull;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Implement a custom model for the table.
 */
public class LogTableModel extends AbstractTableModel {
    private static final List<String> columnNames = List.of("Time", "Type", "Message");
    private static final List<Class<?>> columnClasses = List.of(Instant.class, LogLevel.class, String.class);

    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.Info;
    public static final int DEFAULT_MAX_LOG_LENGTH = 100_000;

    public static final int TIME_COL = 0;
    public static final int TYPE_COL = 1;
    public static final int MSG_COL = 2;

    private final List<List<Object>> data;
    private final List<Object> timeLog;
    private final List<Object> typeLog;
    private final List<Object> messageLog;

    private LogLevel logLevel;
    private int maxLogLength;

    LogTableModel() {
        // create the log arrays
        timeLog = new ArrayList<>();
        typeLog = new ArrayList<>();
        messageLog = new ArrayList<>();

        // create an immutable list for the data
        data = List.of(
                timeLog,
                typeLog,
                messageLog
        );

        logLevel = DEFAULT_LOG_LEVEL;
        maxLogLength = DEFAULT_MAX_LOG_LENGTH;
    }

    /**
     * Add a formatted message to the log if it meets the current log level criteria.
     *
     * @param type   the type of log event
     * @param format the format of the log message
     * @param args   the arguments to the formatter
     */
    synchronized void log(final @NotNull LogLevel type, final @NotNull String format, Object... args) {
        // if the item to be logged is below logLevel, don't add it to the log
        if (type.compareTo(logLevel) < 0) {
            return;
        }

        // remove first message
        if (getRowCount() == maxLogLength) {
            timeLog.remove(0);
            typeLog.remove(0);
            messageLog.remove(0);

            fireTableRowsDeleted(0, 0);
        }

        int index = timeLog.size();

        timeLog.add(Instant.now());
        typeLog.add(type);
        messageLog.add(String.format(format, args));

        fireTableRowsInserted(index, index);
    }

    void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    void setMaxLogLength(int maxLogLength) {
        this.maxLogLength = maxLogLength;

        cullExcessLogMessages();
    }

    private synchronized void cullExcessLogMessages() {
        int elementsToCull = getRowCount() - maxLogLength;

        if (elementsToCull > 0) {
            timeLog.subList(0, elementsToCull).clear();
            typeLog.subList(0, elementsToCull).clear();
            messageLog.subList(0, elementsToCull).clear();
        }

        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return data.size();
    }

    @Override
    public int getRowCount() {
        return messageLog.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(columnIndex).get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
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
