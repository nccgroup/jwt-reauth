// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth.ui.scope;

/**
 * Enum representing the different states we can filter the "active" column on.
 * Any will allow any state.
 * Active allows only rows where the url is in scope.
 * Deactivated allows only rows where the url is marked as not in scope.
 */
public enum ScopeFilter {
    Any,
    Active,
    Deactivated,
}
