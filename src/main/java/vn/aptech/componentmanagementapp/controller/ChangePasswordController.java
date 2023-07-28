package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChangePasswordController {
    @FXML
    private HBox hbox_addButtonGroup;

    @FXML
    private Label lbl_error_confirmPassword;

    @FXML
    private Label lbl_error_newPassword;

    @FXML
    private Label lbl_error_oldPassword;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private MFXTextField txt_confirmPassword;

    @FXML
    private MFXTextField txt_newPassword;

    @FXML
    private MFXTextField txt_oldPassword;
}
