// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.misc;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A JScrollPane which automatically scrolls to the bottom of
 * the text when something new is added.
 */
public class AutoScrollPane extends JScrollPane {
    public AutoScrollPane(Component view) {
        super(view);

        // has to be atomic to be changed in a lambda
        AtomicInteger verticalScrollMax = new AtomicInteger(getVerticalScrollBar().getMaximum());
        getVerticalScrollBar().addAdjustmentListener(e -> {
            if ((e.getAdjustable().getMaximum() - verticalScrollMax.get()) == 0) {
                return;
            }

            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            verticalScrollMax.set(getVerticalScrollBar().getMaximum());
        });
    }
}
