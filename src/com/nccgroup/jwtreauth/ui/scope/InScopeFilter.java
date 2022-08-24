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

import javax.swing.*;

/**
 * Filter class which checks the contents of the inScope column.
 */
class InScopeFilter extends RowFilter<ScopeTableModel, Object> {
    private ScopeFilter filter;

    @Override
    public boolean include(Entry<? extends ScopeTableModel, ?> entry) {
        var value = entry.getValue(ScopeTableModel.IN_SCOPE_COL);

        // if the column value is not a Boolean then something has gone wrong so we filter it out
        if (!(value instanceof Boolean)) {
            return false;
        }

        var active = (boolean) value;

        // if the filter is not set then just allow all
        if (filter == null || filter.equals(ScopeFilter.Any)) {
            return true;
        }

        switch (filter) {
            case Active:
                return active;
            case Deactivated:
                return !active;
            default:
                // this case should be impossible but returning false is more lenient
                return false;
        }
    }

    public void setFilter(ScopeFilter newFilter) {
        if (newFilter != this.filter) {
            this.filter = newFilter;
        }
    }

    public ScopeFilter getFilter() {
        return this.filter;
    }
}
