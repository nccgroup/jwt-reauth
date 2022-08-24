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
