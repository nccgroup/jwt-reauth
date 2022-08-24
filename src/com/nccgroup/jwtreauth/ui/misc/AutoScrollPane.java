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
