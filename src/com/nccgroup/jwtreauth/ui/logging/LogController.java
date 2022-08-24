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

public class LogController {
    private final LogTableModel logTableModel;
    private final LogTable logTable;
    private final LogViewPanel logViewPanel;

    public LogController() {
        this.logTable = new LogTable();
        this.logTableModel = (LogTableModel) logTable.getModel();
        this.logViewPanel = new LogViewPanel(this);
    }

    public LogViewPanel getLogViewPanel() {
        return this.logViewPanel;
    }

    public LogTable getLogTable() {
        return this.logTable;
    }

    public void setLogLevel(LogLevel newLevel) {
        logTableModel.setLogLevel(newLevel);
    }

    public void setMaxLogLength(int newLength) {
        logTableModel.setMaxLogLength(newLength);
    }

    /**
     * Add a formatted debug message to the log
     *
     * @param format the format for the debug message
     * @param args   the items to format
     */
    public void debug(String format, Object... args) {
        logTableModel.log(LogLevel.Debug, format, args);
    }

    /**
     * Add a formatted info message to the log
     *
     * @param format the format for the info message
     * @param args   the items to format
     */
    public void info(String format, Object... args) {
        logTableModel.log(LogLevel.Info, format, args);
    }

    /**
     * Add a formatted error message to the log
     *
     * @param format the format for the error message
     * @param args   the items to format
     */
    public void error(String format, Object... args) {
        logTableModel.log(LogLevel.Error, format, args);
    }
}
