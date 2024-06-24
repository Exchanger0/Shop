package com.shop.client.elements;

import com.shop.client.Starter;

public class RegMenu extends RegLogMenu{
    public RegMenu(boolean isReg, StartMenu parent, Starter starter) {
        super(isReg, parent, starter);
        sendButton.setOnAction(e ->
                starter.getController().registration(usernameField.getText().trim(), passwordField.getText().trim()));
    }
}
