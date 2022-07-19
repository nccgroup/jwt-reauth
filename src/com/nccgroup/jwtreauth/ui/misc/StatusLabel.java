// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.misc;

import javax.swing.*;
import java.awt.*;

/**
 * This is a simple wrapper around the label component, which just sets the text/background
 * based on the current status. It is intended to be used with KeyListeners / other
 * listeners which can produce a lot of errors quickly but where the errors aren't
 * too important, so just knowing whether the current content is allowed or not is
 * sufficient.
 */
public class StatusLabel extends JLabel {
    private static final Color DEFAULT_OK_COLOR = new Color(0xB0FF57);
    private static final Color DEFAULT_ERROR_COLOR = new Color(0xFF7961);

    public enum Status {
        OK,
        ERROR,
    }

    private Status status;
    private final Color okColour;
    private final Color errorColor;

    /**
     * Secondary constructor allowing for custom colours to be picked for the ok / error status.
     *
     * @param okColour   the colour to display as the background for an OK status
     * @param errorColor the colour to display as the background for an ERROR status
     */
    public StatusLabel(Color okColour, Color errorColor) {
        super("", SwingConstants.CENTER);

        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // this is necessary so the background actually shows
        setOpaque(true);
        setPreferredSize(new Dimension(50, 0));

        this.okColour = okColour;
        this.errorColor = errorColor;

        setStatus(Status.OK);
    }

    /**
     * Default constructor provides a light green for an ok status
     * and a light red for an error status.
     */
    public StatusLabel() {
        this(DEFAULT_OK_COLOR, DEFAULT_ERROR_COLOR);
    }

    /**
     * Set the status of the label, changes the text and background color as appropriate.
     *
     * @param newStatus the status to update to
     */
    public void setStatus(Status newStatus) {
        if (newStatus == this.status) {
            // if the status hasn't changed then don't do any work
            return;
        } else {
            this.status = newStatus;
        }

        SwingUtilities.invokeLater(this::updateGUI);
    }

    /**
     * Helper method to handle updating how the label looks.
     */
    private void updateGUI() {
        switch (status) {
            case OK:
                setBackground(this.okColour);
                setText("OK");
                break;
            case ERROR:
                setBackground(this.errorColor);
                setText("ERROR");
                break;
        }
    }
}
