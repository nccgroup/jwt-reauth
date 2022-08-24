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

package com.nccgroup.jwtreauth.ui.settings;

import com.nccgroup.jwtreauth.ui.misc.KeyReleasedListener;
import com.nccgroup.jwtreauth.ui.misc.StatusLabel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class RowBuilder {
    private final SettingsViewPanel settingsViewPanel;
    private final String rowID;
    private JLabel label;
    private JTextField field;
    private StatusLabel status;
    private KeyReleasedStatusHandler statusHandler;
    private KeyReleasedHandler handler;

    RowBuilder(SettingsViewPanel settingsViewPanel, String rowID) {
        this.settingsViewPanel = settingsViewPanel;
        this.rowID = rowID;
        label = null;
        field = null;
        status = null;
        statusHandler = null;
        handler = null;
    }

    RowBuilder setLabelText(String labelText) {
        label = new JLabel(labelText);
        return this;
    }

    RowBuilder setFieldText(String fieldText) {
        field = new JTextField(fieldText);
        return this;
    }

    RowBuilder addStatusLabel() {
        status = new StatusLabel();
        return this;
    }

    RowBuilder setKeyReleasedStatusHandler(KeyReleasedStatusHandler handler) {
        if (this.handler != null) {
            throw new RuntimeException("Cannot set both a standard and a status handler.");
        }

        statusHandler = handler;
        return this;
    }

    RowBuilder setKeyReleasedHandler(KeyReleasedHandler handler) {
        if (this.statusHandler != null) {
            throw new RuntimeException("Cannot set both a status and a standard handler.");
        }

        this.handler = handler;
        return this;
    }

    /**
     * Finalises the builder, returning the created components.
     *
     * @return an array containing the created components
     */
    Component[] build() {
        List<Component> components = new ArrayList<>();

        if (label != null) {
            components.add(label);
        }

        if (field != null) {
            if (statusHandler != null) {
                field.addKeyListener(new KeyReleasedListener(
                        e -> statusHandler.handleKeyRelease(field, status)
                ));

                settingsViewPanel.updateHandlers.put(
                        rowID,
                        newObj -> {
                            if (newObj instanceof String) {
                                field.setText((String) newObj);
                                statusHandler.handleKeyRelease(field, status);
                            }
                        }
                );
            } else if (handler != null) {
                field.addKeyListener(new KeyReleasedListener(
                        e -> handler.handleKeyRelease(field)
                ));

                settingsViewPanel.updateHandlers.put(
                        rowID,
                        newObj -> {
                            if (newObj instanceof String) {
                                field.setText((String) newObj);
                                handler.handleKeyRelease(field);
                            }
                        }
                );
            }

            components.add(field);
        }

        if (status != null) {
            components.add(status);
        }

        return components.toArray(new Component[0]);
    }
}
