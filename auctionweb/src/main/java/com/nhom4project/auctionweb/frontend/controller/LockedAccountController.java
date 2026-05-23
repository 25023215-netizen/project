package com.nhom4project.auctionweb.frontend.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LockedAccountController {

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
