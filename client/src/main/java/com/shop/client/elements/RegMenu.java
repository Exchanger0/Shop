package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.common.UserType;

public class RegMenu extends RegLogMenu{
    public RegMenu(boolean isReg, StartMenu parent, Starter starter) {
        super(isReg, parent, starter);
        sendButton.setOnAction(e -> {
            UserType userType = (UserType) toggleGroup.getSelectedToggle().getUserData();
            starter.getController().registration(userType,
                    usernameField.getText().trim(), passwordField.getText().trim());
        });
    }
}
