package vn.aptech.componentmanagementapp.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class OrderDetailController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    void backButtonOnClick() {
        stage.close();
    }
}
