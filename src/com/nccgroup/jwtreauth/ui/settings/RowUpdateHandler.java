// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.settings;

/**
 * SAM interface to represent a function which can be used to update the state of a row.
 */
interface RowUpdateHandler {
    void update(final Object newData);
}
