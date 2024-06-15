package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.common.UserType;

public class LogInMenu extends RegLogMenu{
    public LogInMenu(boolean isReg, StartMenu parent, Starter starter) {
        super(isReg, parent, starter);
        sendButton.setOnAction(e -> {
            UserType userType = (UserType) toggleGroup.getSelectedToggle().getUserData();
            starter.getController().logIn(userType,
                    usernameField.getText().trim(), passwordField.getText().trim());
        });
    }
}
