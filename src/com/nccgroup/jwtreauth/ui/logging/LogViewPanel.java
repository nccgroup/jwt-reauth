// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.logging;

import com.nccgroup.jwtreauth.ui.misc.AutoScrollPane;
import javax.validation.constraints.NotNull;

import javax.swing.*;
import java.awt.*;

public class LogViewPanel extends JPanel {

    public LogViewPanel(@NotNull LogController logController) {
        super(new BorderLayout());

        LogTable logTable = logController.getLogTable();

        var filterPanel = new LogFilterPanel(logTable);
        var logPane = new AutoScrollPane(logTable);

        add(filterPanel, BorderLayout.PAGE_START);
        add(logPane, BorderLayout.CENTER);
    }

}
