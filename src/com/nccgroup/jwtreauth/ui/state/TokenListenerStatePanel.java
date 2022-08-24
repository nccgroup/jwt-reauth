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

package com.nccgroup.jwtreauth.ui.state;

import burp.IExtensionStateListener;
import com.nccgroup.jwtreauth.TokenListener;
import com.nccgroup.jwtreauth.ui.base.GridColumnPanel;
import javax.validation.constraints.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;

public class TokenListenerStatePanel extends GridColumnPanel implements IExtensionStateListener {
    private static final String TIMER_STOPPED_STRING = "00:00:00";

    private final TokenListener tokenListener;

    private JTextArea tokenArea;
    private JTextArea headerArea;
    private Instant tokenTimeActivated;
    private boolean tokenRefreshScheduled;
    private Duration tokenRefreshDuration;
    private Timer tokenTimer;
    private JLabel tokenTimeActiveLabel;

    public TokenListenerStatePanel(TokenListener tokenListener) {
        super("Listener State", 2, true, 1);

        this.tokenListener = tokenListener;

        addComponents();
    }

    /**
     * Helper function to add all the components to the panel.
     */
    void addComponents() {
        // create the token row
        var titleLabel = new JLabel("token: ");
        this.tokenArea = new JTextArea(TokenListener.DEFAULT_TOKEN_MISSING);
        this.tokenArea.setFont(Font.decode("MONOSPACED"));
        this.tokenArea.setEditable(false);
        this.tokenArea.setLineWrap(true);
        this.tokenArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        // create a panel to hold the buttons
        var buttonPanel = new JPanel(new BorderLayout(5, 0));

        // button for copying the token
        var copyButton = new JButton("Copy token");
        copyButton.addActionListener(
                _event -> {
                    Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection sel = new StringSelection(this.tokenArea.getText());
                    clp.setContents(sel, null);
                }
        );

        // button for setting the token from the clipboard
        var setButton = new JButton("Set token from clipboard");
        setButton.addActionListener(
                _event -> {
                    Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    var trans = clp.getContents(null);
                    if (trans != null) {
                        String s = null;

                        try {
                            s = (String) trans.getTransferData(DataFlavor.stringFlavor);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(setButton, "Failed to read String data from the clipboard.");
                            return;
                        }

                        tokenListener.setTokenManual(s);
                    }
                }
        );
        buttonPanel.add(setButton, BorderLayout.EAST);
        buttonPanel.add(copyButton, BorderLayout.WEST);

        // add the components to their respective grids
        addRow(titleLabel, new JPanel(), this.tokenArea, buttonPanel);

        // add a row for the timer
        this.tokenTimeActiveLabel = new JLabel(TIMER_STOPPED_STRING);
        this.tokenRefreshDuration = Duration.ofSeconds(TokenListener.DEFAULT_AUTH_REQ_DELAY);
        this.tokenTimer = new Timer(500, _event -> {
            var now = Instant.now();
            var timeActive = Duration.between(this.tokenTimeActivated, now);

            this.tokenTimeActiveLabel.setText(
                    String.format(
                            "%02d:%02d:%02d",
                            timeActive.toHoursPart(),
                            timeActive.toMinutesPart(),
                            timeActive.toSecondsPart()
                    )
            );

            // attempt to refresh the token if is greater than the refresh duration
            // and it wasn't set manually
            if (!tokenListener.isTokenSetManually() && !tokenRefreshScheduled && timeActive.compareTo(tokenRefreshDuration) >= 0) {
                // schedule a token refresh
                tokenListener.scheduleTokenRefresh();
                tokenRefreshScheduled = true;
            }
        });

        addRow(new JLabel("token time active: "), this.tokenTimeActiveLabel);

        // create the header row
        this.headerArea = addRowWithTextAreaAndButton(
                "header: ",
                TokenListener.DEFAULT_HEADER_MISSING,
                "Copy header",
                _event -> {
                    Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection sel = new StringSelection(this.headerArea.getText());
                    clp.setContents(sel, null);
                }
        );
    }

    /**
     * Set the text for the token field.
     *
     * @param text the text to set on the tokenField.
     */
    public void updateToken(@NotNull String text, boolean setManually) {
        this.tokenArea.setText(text);

        // if we aren't listening then don't start the timer
        var isListening = tokenListener.isListening();

        // we should stop the timer if the message is the token missing message
        var startTimer = !text.equals(TokenListener.DEFAULT_TOKEN_MISSING);
        if (startTimer && isListening) {
            tokenTimeActivated = Instant.now();
            tokenRefreshScheduled = false;
            tokenTimer.start();
        } else {
            tokenTimer.stop();
            tokenTimeActiveLabel.setText(TIMER_STOPPED_STRING);
        }
    }

    /**
     * Set the text for the header field.
     *
     * @param text the text to set on the headerField.
     */
    public void setHeaderFieldText(@NotNull String text) {
        this.headerArea.setText(text);
    }

    /**
     * Set the duration after which each token should be refreshed.
     *
     * @param tokenRefreshDuration the length of time after which to refresh the token
     */
    public void setTokenRefreshDuration(@NotNull Duration tokenRefreshDuration) {
        this.tokenRefreshDuration = tokenRefreshDuration;
    }

    /**
     * Stop the Timer so that the extension can be unloaded safely.
     */
    public void stopTimer() {
        this.tokenTimer.stop();
    }

    @NotNull
    JTextArea addRowWithTextAreaAndButton(String labelText, String fieldText, String buttonText, ActionListener onPress) {
        // create a label explaining what the box is for
        var titleLabel = new JLabel(labelText);

        // create a text field to hold the text
        var textArea = new JTextArea(fieldText);
        textArea.setFont(Font.decode("MONOSPACED"));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        // create a panel to hold the buttons
        var button = new JButton(buttonText);
        button.addActionListener(onPress);

        // add the components to their respective grids
        addRow(titleLabel, new JPanel(), textArea, button);

        return textArea;
    }

    @Override
    public void extensionUnloaded() {
        stopTimer();
    }
}
