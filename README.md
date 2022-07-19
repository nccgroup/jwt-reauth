# JWT Re-auth

Burp plugin to cache authentication tokens from an "auth" URL, and then add them as headers on all requests going to a certain scope.

## Features

The plugin allows settings to be sent to it via context menus from various menus.
This includes using an entire captured request to acquire new authentication tokens.

![Screenshot showing a drop-down context menu inside of burpsuite, with the text "Send to JWT re-auth (set auth token)"](images/send-to-extension.png)

All of the settings for the plugin can be controller from the main UI panel:

![Screenshot showing the main UI, it has several rows of settings, with a name next to a text box describing each setting.
Then there are three, rows showing the state of the listener and most recently parsed tokens, with a button to copy the token.
In the bottom third of the screen there is a log showing events from the plugin, as well as buttons to filter them and a search box.](images/ui.png)

There is a seperate UI panel to show the scope:

![Screenshot showing a mostly empty panel with a drop-down spinner to filter the scope items, a search box,
and below that, one row showing a in-scope URL, and an empty row.](images/scope.png)

Finally we can see the plugin attaching a cached authentication token as a header.

![Screenshot shows firefox open with a webpage listing the headers sent to the site, one can be seen called Authorization, which holds the cached auth token.](images/demo.png)