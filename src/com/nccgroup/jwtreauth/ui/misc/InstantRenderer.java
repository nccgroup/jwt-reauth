// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

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
