// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

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
