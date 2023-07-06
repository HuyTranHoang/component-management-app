package vn.aptech.componentmanagementapp;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ComponentManagementController implements Initializable {
    @FXML
    private AnchorPane iconUser;

    @FXML
    private MFXTextField txtEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}