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

