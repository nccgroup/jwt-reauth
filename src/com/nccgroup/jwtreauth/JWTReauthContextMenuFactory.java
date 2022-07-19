// Copyright 2022 Sam Leonard
// SPDX-License-Identifier: Apache-2.0

package com.nccgroup.jwtreauth;

import burp.IContextMenuFactory;
import burp.IContextMenuInvocation;
import burp.IExtensionHelpers;
import com.nccgroup.jwtreauth.ui.logging.LogController;
import com.nccgroup.jwtreauth.ui.scope.ScopeController;
import javax.validation.constraints.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JWTReauthContextMenuFactory implements IContextMenuFactory {
    final IExtensionHelpers helpers;
    final ScopeController scopeController;
    final TokenListener tokenListener;
    final LogController logController;

    public JWTReauthContextMenuFactory(JWTReauth jwtReauth) {
        helpers = jwtReauth.getCallbacks().getHelpers();
        scopeController = jwtReauth.getScopeController();
        tokenListener = jwtReauth.getTokenListener();
        logController = jwtReauth.getLogController();
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        final List<JMenuItem> menuItems = new ArrayList<>();

        switch (invocation.getInvocationContext()) {
            case IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST:
            case IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST:
            case IContextMenuInvocation.CONTEXT_PROXY_HISTORY:
            case IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TREE:
            case IContextMenuInvocation.CONTEXT_TARGET_SITE_MAP_TABLE:
            case IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_RESPONSE:
            case IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_RESPONSE:
                menuItems.add(createAuthMenuItem(invocation));
                menuItems.add(createTokenMenuItem(invocation));
                menuItems.add(createScopeMenuItem(invocation));
                break;

            default:
                // if not in a valid scope, return null, indicating no menu items are to be added
                return null;
        }

        return menuItems;
    }

    /**
     * Create a menu item which sends the selected request to the token listener,
     * and sets the scope components from it.
     *
     * @param invocation the invocation context of the menu1 item
     * @return the created menu item
     */
    private @NotNull JMenuItem createScopeMenuItem(IContextMenuInvocation invocation) {
        // add a menu item for setting the scope URL
        var item = new JMenuItem("Send to JWT re-auth (add to scope)");
        item.addActionListener(e -> {
            var messages = invocation.getSelectedMessages();

            // add all selected messages
            for (var request : messages) {
                var url = helpers.analyzeRequest(request).getUrl();

                // if the exact URL isn't already in the table, add it
                if (!scopeController.contains(url)) {
                    scopeController.addToScope(url);
                }
            }
        });

        return item;
    }

    /**
     * Create a menu item which sends the selected request to the token listener,
     * and sets the auth request components from it.
     *
     * @param invocation the invocation context of the menu item
     * @return the created menu item
     */
    private @NotNull JMenuItem createAuthMenuItem(IContextMenuInvocation invocation) {
        var item = new JMenuItem("Send to JWT re-auth (set auth request)");
        item.addActionListener(e -> {
            var messages = invocation.getSelectedMessages();

            if (messages.length == 1) {
                tokenListener.setAuthorizeRequest(messages[0]);
            }
        });

        return item;
    }

    /**
     * Create a menu item which sends the selected request to the token listener,
     * and sets the current token it.
     *
     * @param invocation the invocation context of the menu item
     * @return the created menu item
     */
    private @NotNull JMenuItem createTokenMenuItem(IContextMenuInvocation invocation) {
        // for a response we allow setting the token value
        var item = new JMenuItem("Send to JWT re-auth (set auth token)");
        item.addActionListener(e -> {
            var messages = invocation.getSelectedMessages();

            if (messages.length == 1) {
                tokenListener.processAuthResponse(messages[0], true);
            }
        });

        return item;
    }
}
