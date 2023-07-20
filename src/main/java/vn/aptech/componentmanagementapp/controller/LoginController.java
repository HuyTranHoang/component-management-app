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
import java.util.HashMap;
import java.util.Map;
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

    private final EmployeeService employeeService = new EmployeeService();
    private final Map<String, LoginInfo> loginInfoMap = new HashMap<>(); // Use HashMap for quick lookup
    private long currentID = -1;
    private final Validator loginValidator = new Validator();
    private final Validator forgotValidator = new Validator();
    private final Validator resetValidator = new Validator();
    private Stage stage;
    private double x;
    private double y;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLoginValidator();
        initForgotValidator();
        initResetValidator();
        initEnterKeyPressing();

        loadLoginInfo();

    }

    private void loadLoginInfo() {
        loginInfoMap.clear();
        for (LoginInfo loginInfo : employeeService.getAllLoginInfo()) {
            loginInfoMap.put(loginInfo.getEmail(), loginInfo);
        }
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
                    if (citizenID.isEmpty())
                        context.error("Citizen ID can't be empty");
                    else if (!citizenID.matches("\\d+"))
                        context.error("Citizen Id can't have letters");
                    else if(!citizenID.matches("^\\d{12}$"))
                        context.error("Citizen Id must have 12 digits");
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
                    if (!newPassword.equals(confirmPassword) && newPassword.length() > 8)
                        context.error("Confirm password does not match");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_reset_confirmNewPasswordError);

        resetValidator.createCheck()
                .dependsOn("new_password", txt_reset_newPassword.textProperty())
                .withMethod(context -> {
                    String newPassword = context.get("new_password");
                    if (newPassword.isEmpty())
                        context.error("New password can't be empty");
                    else if(newPassword.length() < 8 || newPassword.length() > 20)
                        context.error("Password can't be less than 8 or greater than 20 characters");
                    else if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$%])[A-Za-z\\d@$%]{8,}$"))
                        context.error("Password must contain number, uppercase and special characters");
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

    @FXML
    void loginButtonOnClick() {
        if (loginValidator.validate()) {
            String email = txt_login_email.getText();
            String password = hashSHA256(txt_login_password.getText());
            LoginInfo loginInfo = loginInfoMap.get(email);

            if (loginInfo != null && loginInfo.getPassword().equals(password)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/main.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());

                    scene.setOnMousePressed(event -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    scene.setOnMouseDragged(event -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                        stage.setOpacity(.9);
                    });

                    scene.setOnMouseReleased(event -> stage.setOpacity(1));
                    ManagementController controller = fxmlLoader.getController();
                    controller.setCurrentEmployee(employeeService.getEmployeeById(loginInfo.getId()));
                    controller.setStage(stage);
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
            Optional<LoginInfo> optionalLoginInfo = loginInfoMap.values().stream()
                    .filter(loginInfo -> loginInfo.getCitizenId().equals(citizen_id))
                    .findFirst();

            if (optionalLoginInfo.isPresent()) {
                currentID = optionalLoginInfo.get().getId();
                clearReset();

                anchor_leftPanel_login.setVisible(true);
                anchor_leftPanel_forgot.setVisible(false);

                anchor_rightPanel_Login.setVisible(false);
                anchor_rightPanel_forgot.setVisible(false);
                anchor_rightPanel_reset.setVisible(true);

                txt_reset_newPassword.requestFocus();
            } else {
                lbl_forgot_citizenError.setText("This citizen ID doesn't belong to any employee");
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
                loadLoginInfo();
                currentID = -1;
                backToLoginOnClick();
                lbl_login_resetSuccess.setVisible(true);
                lbl_login_resetSuccess2.setVisible(true);
            } else {
                System.out.println("Something went wrong");
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
        confirmation.setContentText("Are you sure you want to exit?");

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
