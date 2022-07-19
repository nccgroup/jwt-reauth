// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.settings;

import com.nccgroup.jwtreauth.ui.misc.StatusLabel;

import javax.swing.*;

/**
 * SAM interface to represent an action which handles a keyReleased event.
 * The handler is provided a textField and a status, to perform validation,
 * update any state and set the status accordingly.
 */
interface KeyReleasedStatusHandler {
    void handleKeyRelease(final JTextField field, final StatusLabel status);
}

