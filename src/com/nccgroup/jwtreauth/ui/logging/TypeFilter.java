// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.logging;

import javax.swing.*;

/**
 * Filter class which checks the contents of the type column
 */
class TypeFilter extends RowFilter<LogTableModel, Object> {
    public final static boolean INCLUDE_ERROR_BY_DEFAULT = true;
    public final static boolean INCLUDE_INFO_BY_DEFAULT = true;
    public final static boolean INCLUDE_DEBUG_BY_DEFAULT = false;

    private boolean filterIncludeInfo;
    private boolean filterIncludeDebug;
    private boolean filterIncludeError;

    public TypeFilter() {
        filterIncludeError = INCLUDE_ERROR_BY_DEFAULT;
        filterIncludeInfo = INCLUDE_INFO_BY_DEFAULT;
        filterIncludeDebug = INCLUDE_DEBUG_BY_DEFAULT;
    }

    public void setFilterInclude(LogLevel level, boolean include) {
        switch (level) {
            case Error:
                this.filterIncludeError = include;
                break;
            case Info:
                this.filterIncludeInfo = include;
                break;
            case Debug:
                this.filterIncludeDebug = include;
                break;
        }
    }

    @Override
    public boolean include(Entry<? extends LogTableModel, ?> entry) {
        var value = entry.getValue(LogTableModel.TYPE_COL);

        // if the object is a LogLevel, then use the currently set booleans to filter
        if (value instanceof LogLevel) {
            var logLevel = (LogLevel) value;
            switch (logLevel) {
                case Info:
                    return filterIncludeInfo;
                case Debug:
                    return filterIncludeDebug;
                case Error:
                    return filterIncludeError;
            }
        }

        // if its not a LogLevel something has gone wrong so just return false and don't include it
        return false;
    }
}
