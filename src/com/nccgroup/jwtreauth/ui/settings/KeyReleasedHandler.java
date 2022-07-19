// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.settings;

import javax.swing.*;

/**
 * SAM interface to represent an action which handles a keyReleased event.
 * The handler is provided a textField, to perform validation, and update any state.
 */
interface KeyReleasedHandler {
    void handleKeyRelease(JTextField field);
}
