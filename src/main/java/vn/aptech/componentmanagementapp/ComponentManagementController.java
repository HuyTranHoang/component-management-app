package vn.aptech.componentmanagementapp;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.LoginInfo;
import vn.aptech.componentmanagementapp.service.EmployeeService;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ComponentManagementController implements Initializable {
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
    private Label btn_forgot_backToLogin;

    @FXML
    private Label btn_login_forgotPassword;

    @FXML
    private MFXButton btn_login_login;

    @FXML
    private Label btn_reset_backToLogin;

    @FXML
    private MFXButton btn_reset_confirm;

    @FXML
    private MFXTextField txt_forgot_citizen;

    @FXML
    private MFXButton txt_forgot_reset;

    @FXML
    private MFXTextField txt_login_email;

    @FXML
    private MFXPasswordField txt_login_password;

    @FXML
    private MFXPasswordField txt_reset_newPassword;

    @FXML
    private MFXPasswordField txt_reset_newPasswordConfirm;

//    Service
    private EmployeeService employeeService;

//    List
    ArrayList<LoginInfo> loginInfos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeService = new EmployeeService();
        loginInfos = (ArrayList<LoginInfo>) employeeService.getAllLoginInfo();

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
        // TODO Validate dữ liệu trước khi get từ textfield
        // TODO Hiển thị thông báo ra ngoài nều sai thông tin đăng nhập

        String email = txt_login_email.getText();
        String password = hashSHA256(txt_login_password.getText());

        boolean isLoginValid = loginInfos.stream()
                .anyMatch(loginInfo -> loginInfo.getEmail().equals(email) && loginInfo.getPassword().equals(password));

        if (isLoginValid) {
            System.out.println("Login successfully!");
        } else {
            System.out.println("Wrong email or password!");
        }

    }
    @FXML
    void forgotPasswordOnclick() {
        anchor_leftPanel_login.setVisible(false);
        anchor_leftPanel_forgot.setVisible(true);

        anchor_rightPanel_Login.setVisible(false);
        anchor_rightPanel_forgot.setVisible(true);
        anchor_rightPanel_reset.setVisible(false);
    }

    @FXML
    void backToLoginOnClick() {
        // TODO Kiểm tra xem có trong database hay không, nếu có chuyển sang màn hình chính
        // TODO Kiểm tra xem có nhập đúng kiểu email trên trường email không
        anchor_leftPanel_login.setVisible(true);
        anchor_leftPanel_forgot.setVisible(false);

        anchor_rightPanel_Login.setVisible(true);
        anchor_rightPanel_forgot.setVisible(false);
        anchor_rightPanel_reset.setVisible(false);
    }

    @FXML
    void resetButtonOnClick() {
        // TODO Kiểm tra xem trong database có tồn tại thẻ căn cước công dân này hay không
        // TODO Kiểm tra xem có nhập đúng kiểu căn cước hay không ( 12 số )
        anchor_leftPanel_login.setVisible(true);
        anchor_leftPanel_forgot.setVisible(false);

        anchor_rightPanel_Login.setVisible(false);
        anchor_rightPanel_forgot.setVisible(false);
        anchor_rightPanel_reset.setVisible(true);
    }

    @FXML
    void confirmButtonOnClick() {
        // TODO Kiểm tra newPassword và newPasswordConfirm có trùng nhau không
        // TODO Kiểm tra độ mạnh mật khẩu ( > 8 ký tự, có chứa số, ký tự đặc biệt )
        backToLoginOnClick();
    }
}