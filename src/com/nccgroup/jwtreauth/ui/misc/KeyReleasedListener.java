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
