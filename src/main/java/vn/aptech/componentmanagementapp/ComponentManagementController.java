package vn.aptech.componentmanagementapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ComponentManagementController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}