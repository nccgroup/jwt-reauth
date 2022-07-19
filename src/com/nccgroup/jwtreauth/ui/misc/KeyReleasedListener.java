// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.misc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Calss implementing a KeyListener which does nothing on KeyPressed,
 * or KeyTyped. And calls a user specified function on KeyReleased.
 */
public class KeyReleasedListener implements KeyListener {
    private final OnKeyRelease action;

    public KeyReleasedListener(OnKeyRelease action) {
        this.action = action;
    }

    /**
     * SAM interface, mirroring the call signature of KeyListener::KeyReleased
     */
    public interface OnKeyRelease {
        void keyReleased(KeyEvent e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        action.keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
