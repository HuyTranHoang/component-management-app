package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.service.EmployeeService;


import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;


public class ChangePasswordController implements Initializable {

    // Label
    @FXML
    private Label lbl_error_confirmPassword;

    @FXML
    private Label lbl_error_newPassword;

    @FXML
    private Label lbl_error_currentPassword;

    @FXML
    private Label lbl_successMessage;

    // Text
    @FXML
    private MFXTextField txt_confirmPassword;

    @FXML
    private MFXTextField txt_newPassword;

    @FXML
    private MFXTextField txt_currentPassword;

    // Variable
    private Employee currentEmployee;
    private Stage stage;
    private long currentID;

    // Service
    private final EmployeeService employeeService = new EmployeeService();

    // Validator
    private final Validator changeValidator = new Validator();

    // Constructor
    public void setStage(Stage stage) {this.stage = stage;}

    public ChangePasswordController() {currentID = -1;}

    // Setter
    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initChangeValidator();
    }

    @FXML
     void changePasswordButtonClick(){
        currentID = currentEmployee.getId();
        if (changeValidator.validate()) {
            if (currentID != -1) {
                long id = currentID;
                String password = hashSHA256(txt_newPassword.getText());
                employeeService.updateEmployeePassword(id, password);
                currentID = -1;

                Alert information = new Alert(Alert.AlertType.INFORMATION);
                information.setTitle("Successfully");
                information.setHeaderText(null);
                information.setContentText("Change password successfully!!!!!");

                ImageView image = null;
                URL resourceURL = ComponentManagementApplication.class.getResource("images/alert/success.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new ImageView(resourcePath);
                }
                image.setFitHeight(50);
                image.setFitWidth(50);

                information.setGraphic(image);
                information.show();
            } else {
                System.out.println("Something went wrong");
            }
            clearValidateError();
            stage.close();
        }
    }
    private void initChangeValidator() {
            changeValidator.createCheck()
                    .dependsOn("current_password", txt_currentPassword.textProperty())
                    .withMethod(context -> {
                        String currentPassword = hashSHA256(txt_currentPassword.getText());
                        String oldPassword = currentEmployee.getPassword();
                        if(currentPassword.isEmpty())
                            context.error("Current password can't be empty");
                         else if (!currentPassword.equals(oldPassword))
                            context.error("Current password is incorrect");
                    })
                    .decoratingWith(this::labelDecorator)
                    .decorates(lbl_error_currentPassword);

        changeValidator.createCheck()
                .dependsOn("new_password", txt_newPassword.textProperty())
                .withMethod(context -> {
                    String newPassword = context.get("new_password");
                    String currentPassword = txt_currentPassword.getText();
                    if (newPassword.isEmpty())
                        context.error("New password can't be empty");
                    else if(newPassword.length() < 8)
                        context.error("Password must be more than 8 characters");
                    else if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$%])[A-Za-z\\d@$%]{8,}$"))
                        context.error("Password must contain number, uppercase and special characters");
                    else if (newPassword.equals(currentPassword))
                        context.error("Password can't match the current password");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_newPassword);

        changeValidator.createCheck()
                .dependsOn("new_password", txt_newPassword.textProperty())
                .dependsOn("confirm_password", txt_confirmPassword.textProperty())
                .withMethod(context -> {
                    String newPassword = context.get("new_password");
                    String confirmPassword = context.get("confirm_password");
                    if  (confirmPassword.isEmpty())
                        context.error("Confirm password can't be empty");
                    else if (!newPassword.equals(confirmPassword))
                        context.error("Confirm password does not match");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_confirmPassword);
    }
    private Decoration labelDecorator(ValidationMessage message) {
        return new Decoration() {
            @Override
            public void add(Node target) {
                ((Label) target).setText(message.getText());
                target.setVisible(true);
            }
            @Override
            public void remove(Node target) {
                target.setVisible(false);
            }
        };
    }
    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void clearValidateError() {
        txt_currentPassword.clear();
        txt_newPassword.clear();
        txt_confirmPassword.clear();
        lbl_error_currentPassword.setVisible(false);
        lbl_error_newPassword.setVisible(false);
        lbl_error_confirmPassword.setVisible(false);
    }
}
