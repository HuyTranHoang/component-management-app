package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.LoginInfo;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane anchor_leftPanel_forgot;

    @FXML
    private AnchorPane anchor_leftPanel_login;

    @FXML
    private AnchorPane anchor_rightPanel_Login;

    @FXML
    private AnchorPane anchor_rightPanel_forgot;

    @FXML
    private AnchorPane anchor_rightPanel_reset;

    @FXML
    private Label lbl_login_emailError;

    @FXML
    private Label lbl_login_passwordError;

    @FXML
    private Label lbl_forgot_citizenError;

    @FXML
    private Label lbl_reset_newPasswordError;

    @FXML
    private Label lbl_reset_confirmNewPasswordError;

    @FXML
    private Label lbl_login_resetSuccess;

    @FXML
    private Label lbl_login_resetSuccess2;

    @FXML
    private MFXTextField txt_forgot_citizen;

    @FXML
    private MFXTextField txt_login_email;

    @FXML
    private MFXPasswordField txt_login_password;

    @FXML
    private MFXPasswordField txt_reset_newPassword;

    @FXML
    private MFXPasswordField txt_reset_newPasswordConfirm;

    //    Service
    private final EmployeeService employeeService = new EmployeeService();

    //    List
    private ArrayList<LoginInfo> loginInfos;
    private long currentID = -1;

    //    Validator
    private final Validator loginValidator = new Validator();
    private final Validator forgotValidator = new Validator();
    private final Validator resetValidator = new Validator();

//    Variable
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //    Init function
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLoginValidator();
        initForgotValidator();
        initResetValidator();

        initEnterKeyPressing();

        loginInfos = (ArrayList<LoginInfo>) employeeService.getAllLoginInfo();
    }

    private void initEnterKeyPressing() {
        EventHandler<KeyEvent> loginEventHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButtonOnClick();
            }
        };
        EventHandler<KeyEvent> passwordConfirmEventHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButtonOnClick();
            }
        };

        txt_login_email.setOnKeyPressed(loginEventHandler);
        txt_login_password.setOnKeyPressed(loginEventHandler);

        txt_forgot_citizen.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                resetButtonOnClick();
            }
        });

        txt_reset_newPassword.setOnKeyPressed(passwordConfirmEventHandler);
        txt_reset_newPasswordConfirm.setOnKeyPressed(passwordConfirmEventHandler);
    }

    //    Validator section
    private void initLoginValidator() {
        loginValidator.createCheck()
                .dependsOn("email", txt_login_email.textProperty())
                .withMethod(context -> {
                    String email = context.get("email");
                    if (email.isEmpty()) {
                        context.error("Email can't be empty");
                    } else if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        context.error("Incorrect email format");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_login_emailError);
    }

    private void initForgotValidator() {
        forgotValidator.createCheck()
                .dependsOn("citizen_id", txt_forgot_citizen.textProperty())
                .withMethod(context -> {
                    String citizenID = context.get("citizen_id");
                    if (citizenID.isEmpty()) {
                        context.error("Citizen ID can't be empty");
                    } else if (!citizenID.matches("^\\d{12}$")) {
                        context.error("Citizen ID must have 12 numbers");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_forgot_citizenError);
    }

    private void initResetValidator() {
        resetValidator.createCheck()
                .dependsOn("new_password", txt_reset_newPassword.textProperty())
                .dependsOn("confirm_new_password", txt_reset_newPasswordConfirm.textProperty())
                .withMethod(context -> {
                    String newPassword = context.get("new_password");
                    String confirmPassword = context.get("confirm_new_password");
                    if (!newPassword.equals(confirmPassword) && newPassword.length() > 8) {
                        context.error("Confirm password does not match");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_reset_confirmNewPasswordError);

        resetValidator.createCheck()
                .dependsOn("new_password", txt_reset_newPassword.textProperty())
                .withMethod(context -> {
                    String newPassword = context.get("new_password");
                    if (newPassword.isEmpty()) {
                        context.error("New password can't be empty");
                    } else if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
                        context.error("Must have at least 8 characters, contain letters and numbers");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_reset_newPasswordError);
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

    /**
     * @param input String trước khi được mã hoá
     * @return String đã được mã hoá SHA-256
     */
    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // Convert the byte array to a hexadecimal string
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

    @FXML
    void loginButtonOnClick() {
        if (loginValidator.validate()) {
            String email = txt_login_email.getText();
            String password = hashSHA256(txt_login_password.getText());
            boolean isLoginValid = loginInfos.stream()
                    .anyMatch(loginInfo -> loginInfo.getEmail().equals(email) && loginInfo.getPassword().equals(password));

            if (isLoginValid) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("main.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    stage.centerOnScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                lbl_login_passwordError.setText("Wrong email or password!");
                lbl_login_passwordError.setVisible(true);
            }
        }

    }

    @FXML
    void forgotPasswordOnclick() {
        clearForgot();

        anchor_leftPanel_login.setVisible(false);
        anchor_leftPanel_forgot.setVisible(true);

        anchor_rightPanel_Login.setVisible(false);
        anchor_rightPanel_forgot.setVisible(true);
        anchor_rightPanel_reset.setVisible(false);

        txt_forgot_citizen.requestFocus();
    }

    @FXML
    void backToLoginOnClick() {
        clearLogin();

        anchor_leftPanel_login.setVisible(true);
        anchor_leftPanel_forgot.setVisible(false);

        anchor_rightPanel_Login.setVisible(true);
        anchor_rightPanel_forgot.setVisible(false);
        anchor_rightPanel_reset.setVisible(false);

        txt_login_email.requestFocus();
    }

    @FXML
    void resetButtonOnClick() {
        if (forgotValidator.validate()) {
            String citizen_id = txt_forgot_citizen.getText();
            Optional<String> optionalId = loginInfos.stream()
                    .filter(loginInfo -> loginInfo.getCitizen_id().equals(citizen_id))
                    .map(LoginInfo::getId)
                    .findFirst();

            if (optionalId.isPresent()) {
                currentID = Long.parseLong(optionalId.get());
                clearReset();

                anchor_leftPanel_login.setVisible(true);
                anchor_leftPanel_forgot.setVisible(false);

                anchor_rightPanel_Login.setVisible(false);
                anchor_rightPanel_forgot.setVisible(false);
                anchor_rightPanel_reset.setVisible(true);

                txt_reset_newPassword.requestFocus();
            } else {
                lbl_forgot_citizenError.setText("This citizen ID don't belong to any employee");
                lbl_forgot_citizenError.setVisible(true);
            }
        }
    }

    @FXML
    void confirmButtonOnClick() {
        if (resetValidator.validate()) {
            if (currentID != -1) {
                long id = currentID;
                String password = hashSHA256(txt_reset_newPassword.getText());
                employeeService.updateEmployeePassword(id, password);
//                Cập nhật lại list login sau khi update
                loginInfos = (ArrayList<LoginInfo>) employeeService.getAllLoginInfo();
                currentID = -1;
                backToLoginOnClick();
//                Set thông báo thành công cập nhật password
                lbl_login_resetSuccess.setVisible(true);
                lbl_login_resetSuccess2.setVisible(true);
            } else {
                System.out.println("something is wrong");
            }
        }
    }

    private void clearLogin() {
        txt_login_email.setText("");
        txt_login_password.setText("");
        lbl_login_emailError.setVisible(false);
        lbl_login_passwordError.setVisible(false);
        lbl_login_resetSuccess.setVisible(false);
        lbl_login_resetSuccess2.setVisible(false);
    }

    private void clearForgot() {
        txt_forgot_citizen.setText("");
        lbl_forgot_citizenError.setVisible(false);
    }

    private void clearReset() {
        txt_reset_newPassword.setText("");
        txt_reset_newPasswordConfirm.setText("");
        lbl_reset_newPasswordError.setVisible(false);
        lbl_reset_confirmNewPasswordError.setVisible(false);
    }

    @FXML
    void exitButtonOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure want to exit?");

        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            DatabaseConnection.closeConnection(DatabaseConnection.getConnection());
            stage.close();
        }
    }

    @FXML
    void minimizeButtonOnClick() {
        stage.setIconified(true);
    }
}