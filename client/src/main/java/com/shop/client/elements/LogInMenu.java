package com.shop.client.elements;

import com.shop.client.Starter;

public class LogInMenu extends RegLogMenu{
    public LogInMenu(boolean isReg, StartMenu parent, Starter starter) {
        super(isReg, parent, starter);
        sendButton.setOnAction(e ->
                starter.getController().logIn(usernameField.getText().trim(), passwordField.getText().trim()));
    }
}
