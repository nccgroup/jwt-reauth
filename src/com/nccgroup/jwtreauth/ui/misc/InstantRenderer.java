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

package com.nccgroup.jwtreauth.ui.misc;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantRenderer extends DefaultTableCellRenderer {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter
            .ofPattern("HH:mm:ss dd MMM yyyy")
            .withZone(ZoneId.systemDefault());

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
        final var result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Instant) {
            setText(FORMAT.format((Instant) value));
        } else {
            setText("");
        }

        return result;
    }
}
