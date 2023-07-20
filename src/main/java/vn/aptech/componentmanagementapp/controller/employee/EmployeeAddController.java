package vn.aptech.componentmanagementapp.controller.employee;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.DepartmentService;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.service.PositionService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class EmployeeAddController implements Initializable {

    @FXML
    private MFXFilterComboBox<Department> cbb_department;

    @FXML
    private MFXFilterComboBox<Position> cbb_position;

    @FXML
    private HBox hbox_addButtonGroup;

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private ImageView imageView_image;

    private File selectedImageFile;

    @FXML
    private Label lbl_error_citizenId;

    @FXML
    private Label lbl_error_department;

    @FXML
    private Label lbl_error_email;

    @FXML
    private Label lbl_error_name;

    @FXML
    private Label lbl_error_address;

    @FXML
    private Label lbl_error_password;

    @FXML
    private Label lbl_error_phone;

    @FXML
    private Label lbl_error_position;

    @FXML
    private Label lbl_error_salary;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private MFXTextField txt_address;

    @FXML
    private MFXTextField txt_citizenId;

    @FXML
    private MFXDatePicker txt_dateOfBirth;

    @FXML
    private MFXDatePicker txt_dateOfHire;

    @FXML
    private MFXTextField txt_email;

    @FXML
    private MFXTextField txt_name;

    @FXML
    private MFXPasswordField txt_password;

    @FXML
    private MFXTextField txt_phone;

    @FXML
    private MFXTextField txt_salary;
    // List
    private ObservableList<Department> departments;
    private ObservableList<Position> positions;

    // Validator
    Validator validator = new Validator();

    // Service
    private final EmployeeService employeeService = new EmployeeService();
    private final DepartmentService departmentService = new DepartmentService();
    private final PositionService positionService = new PositionService();

    // Truyền từ ngoài vào
    private TableView<Employee> tableView;

    public void setTableView(TableView<Employee> tableView) {
        this.tableView = tableView;
    }

    // Cache view
    private AnchorPane employeeView;

    public void setEmployeeView(AnchorPane employeeView) {
        this.employeeView = employeeView;
    }

    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBox();
        initValidator();
    }

    private void initComboBox() {
        List<Department> departmentList = departmentService.getAllDepartment();
        departments = FXCollections.observableArrayList(departmentList);

        List<Position> positionList = positionService.getAllDepartment();
        positions = FXCollections.observableArrayList(positionList);

        cbb_department.setItems(departments);
        cbb_department.getSelectionModel().selectFirst();
        cbb_position.setItems(positions);
        cbb_position.getSelectionModel().selectFirst();
    }

    private void initValidator() {
        validator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String name = context.get("name");
                    if (name.isEmpty())
                        context.error("Name can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_name);

        validator.createCheck()
                .dependsOn("address", txt_address.textProperty())
                .withMethod(context -> {
                    String address = context.get("address");
                    if (address.isEmpty())
                        context.error("Address can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_address);

        validator.createCheck()
                .dependsOn("phone", txt_phone.textProperty())
                .withMethod(context -> {
                    String phone = context.get("phone");
                    if (phone.isEmpty())
                        context.error("Phone can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_phone);

        validator.createCheck()
                .dependsOn("email", txt_email.textProperty())
                .withMethod(context -> {
                    String email = context.get("email");
                    if (email.isEmpty())
                        context.error("Email can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_email);

        validator.createCheck()
                .dependsOn("salary", txt_salary.textProperty())
                .withMethod(context -> {
                    String salary = context.get("salary");
                    if (salary.isEmpty())
                        context.error("Salary can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_salary);

        validator.createCheck()
                .dependsOn("citizenId", txt_citizenId.textProperty())
                .withMethod(context -> {
                    String citizenId = context.get("citizenId");
                    if (citizenId.isEmpty())
                        context.error("Citizen Id can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_citizenId);

        validator.createCheck()
                .dependsOn("password", txt_password.textProperty())
                .withMethod(context -> {
                    String password = context.get("password");
                    if (password.isEmpty())
                        context.error("Password can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_password);

        // Validate date of birth lớn hơn 18 tuổi, date of hire hông được trong quá khứ
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

    @FXML
    void chooseImageButtonOnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView_image.setImage(image);
        }
    }


    @FXML
    private void saveImage() {
        if (selectedImageFile != null) {
            try {
                // Tạo URI từ đường dẫn tương đối của tấm hình đã chọn
                URI selectedImageUri = selectedImageFile.toURI();

                // Lấy đường dẫn tới thư mục lưu trữ tấm hình trong thư mục gốc của ứng dụng
                String directoryPath = "images/employee/";

                // Tạo thư mục nếu chưa tồn tại
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        System.out.println("Thư mục đã được tạo: " + directory.getAbsolutePath());
                    } else {
                        System.out.println("Không thể tạo thư mục lưu trữ tấm hình!");
                        return;
                    }
                }

                String originalName = selectedImageFile.getName();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID() + extension;

                // Đường dẫn tới tấm hình mới trong thư mục lưu trữ
                String destinationPath = directoryPath + uniqueFileName;
                File destination = new File(destinationPath);

                Files.copy(selectedImageUri.toURL().openStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Lưu tên tấm hình vào cơ sở dữ liệu (giả sử bạn đã có cơ sở dữ liệu ở đây)
                String imageName = uniqueFileName;
                System.out.println("Tên tấm hình: " + imageName);
                System.out.println("Lưu thành công!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Lưu thất bại: " + e.getMessage());
            }
        } else {
            System.out.println("Hãy chọn một tấm hình trước khi lưu!");
        }
    }

    @FXML
    void clearInput() {

    }

    @FXML
    void listEmployeeButtonOnClick() {
        if (employeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/employee/employee.fxml"));
                employeeView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(employeeView);
    }

    @FXML
    void listProductButtonOnClick() {

    }

    @FXML
    void storeButtonOnClick() {

    }

    @FXML
    void updateButtonOnClick() {

    }

}
