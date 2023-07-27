package vn.aptech.componentmanagementapp.controller.employee;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.service.DepartmentService;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.service.PositionService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class EmployeeAddController implements Initializable {

    // Call back add employee
    public interface EmployeeAddCallback {
        void onEmployeeAdded(Employee employee);
    }
    private EmployeeAddCallback employeeAddCallback;
    public void setEmployeeAddCallback(EmployeeAddCallback employeeAddCallback) {
        this.employeeAddCallback = employeeAddCallback;
    }

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
    private Label lbl_error_dateOfBirth;

    @FXML
    private Label lbl_error_dateOfHire;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private Label lbl_error_newPassword;

    @FXML
    private Label lbl_error_confirmPassword;

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
    private MFXPasswordField txt_newPassword;

    @FXML
    private MFXPasswordField txt_confirmPassword;

    @FXML
    private MFXTextField txt_phone;

    @FXML
    private MFXTextField txt_salary;
    // List
    private ObservableList<Department> departments;
    private ObservableList<Position> positions;

    private Employee currentEmployee;

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    // Validator
    Validator employeeValidator = new Validator();
    private Boolean isUpdate = false;
    private Boolean isUploadNewImage = false;

    private ObservableList<Employee> employees;

    public void setEmployees(ObservableList<Employee> employees) {
        this.employees = employees;
    }

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
        txt_dateOfBirth.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_dateOfBirth.getLocale()));
        txt_dateOfHire.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_dateOfHire.getLocale()));

        initComboBox();
        initValidator();
    }

    private void initComboBox() {
        List<Department> departmentList = departmentService.getAllDepartment();
        departments = FXCollections.observableArrayList(departmentList);

        List<Position> positionList = positionService.getAllPosition();
        positions = FXCollections.observableArrayList(positionList);

        cbb_department.setItems(departments);
        cbb_department.getSelectionModel().selectFirst();
        cbb_position.setItems(positions);
        cbb_position.getSelectionModel().selectFirst();
    }

    private void initValidator() {
        employeeValidator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String name = context.get("name");
                    if (name.isEmpty())
                        context.error("Name can't be empty");
                    else if (name.matches("\\d+"))
                        context.error("Name can't contain digits");
                    else if (name.length() > 255)
                        context.error("Name length exceeds the maximum limit of 255 characters");

                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_name);

        employeeValidator.createCheck()
                .dependsOn("address", txt_address.textProperty())
                .withMethod(context -> {
                    String address = context.get("address");
                    if (address.isEmpty())
                        context.error("Address can't be empty");
                    else if (address.length() > 255)
                        context.error("Address length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_address);

        employeeValidator.createCheck()
                .dependsOn("phone", txt_phone.textProperty())
                .withMethod(context -> {
                    String phone = context.get("phone");
                    if (phone.isEmpty())
                        context.error("Phone can't be empty");
                    else if (!phone.matches("^\\d{10}$"))
                        context.error("Phone must have 10 digits");
                    else if (isUpdate ? !isPhoneUniqueUpdate(employees, phone) : !isPhoneUnique(employees, phone))
                        context.error("This phone number is already in the database");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_phone);

        employeeValidator.createCheck()
                .dependsOn("email", txt_email.textProperty())
                .withMethod(context -> {
                    String email = context.get("email");
                    if(email.isEmpty())
                        context.error("Email can't be empty");
                     else if (!email.matches("^(|([A-Za-z0-9._%+-]+@gmail\\.com))$"))
                        context.error("Please enter a valid email address");
                     else if (email.length() > 255)
                        context.error("Email length exceeds the maximum limit of 255 characters");
                    else if (isUpdate ? !isEmailUniqueUpdate(employees, email) : !isEmailUnique(employees, email)) {
                        context.error("This email is already in the database");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_email);

        employeeValidator.createCheck()
                .dependsOn("salary", txt_salary.textProperty())
                .withMethod(context -> {
                    String salary = context.get("salary");
                    if (salary.isEmpty())
                        context.error("Salary can't be empty");
                    else if(!salary.matches("\\d+"))
                        context.error("Salary can't contain characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_salary);

        employeeValidator.createCheck()
                .dependsOn("citizenId", txt_citizenId.textProperty())
                .withMethod(context -> {
                    String citizenId = context.get("citizenId");
                    if (citizenId.isEmpty())
                        context.error("Citizen Id can't be empty");
                    else if(!citizenId.matches("^\\d{12}$"))
                        context.error("Citizen Id must have 12 digits");
                    else if (isUpdate ? !isCitizenIdUniqueUpdate(employees, citizenId) : !isCitizenIdUnique(employees, citizenId))
                        context.error("This citizen Id is already in the database");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_citizenId);

        employeeValidator.createCheck()
                .dependsOn("password", txt_password.textProperty())
                .withMethod(context -> {
                    String password = context.get("password");
                    if (!isUpdate) {
                        if (password.isEmpty()) {
                            context.error("Password can't be empty");
                        } else if (password.length() < 8) {
                            context.error("Password must be more than 8 characters");
                        } else if (!isPasswordStrong(password)) {
                            context.error("Password must contain number, uppercase and special characters");
                        }
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_password);

        employeeValidator.createCheck()
                .dependsOn("password", txt_password.textProperty())
                .dependsOn("confirm_password", txt_confirmPassword.textProperty())
                .withMethod(context -> {
                    String password = context.get("password");
                    String confirmPassword = context.get("confirm_password");
                    if (!password.equals(confirmPassword) && password.length() > 8)
                        context.error("Confirm password does not match");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_confirmPassword);

        employeeValidator.createCheck()
                .dependsOn("dateOfBirth", txt_dateOfBirth.valueProperty())
                .withMethod(context -> {
                    LocalDate dateOfBirth = context.get("dateOfBirth");
                    if(dateOfBirth == null)
                        context.error("Date of birth can't be empty");
                    else if (dateOfBirth.isAfter(LocalDate.now().minusYears(18)))
                        context.error("Employee must be at least 18 years old");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_dateOfBirth);

        employeeValidator.createCheck()
                .dependsOn("dateOfHire", txt_dateOfHire.valueProperty())
                .withMethod(context -> {
                    LocalDate dateOfHire = context.get("dateOfHire");
                   if(dateOfHire == null)
                       context.error("Date of hire can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_dateOfHire);
    }

    private boolean isEmailUnique(List<Employee> employees, String txt_email) {
        return employees.stream()
                .noneMatch(employee -> employee.getEmail() != null && employee.getEmail().equals(txt_email));
    }

    private boolean isEmailUniqueUpdate(List<Employee> employees, String txt_email) {
        String email = currentEmployee.getEmail();
        return employees.stream()
                .noneMatch(employee -> employee.getEmail() != null && employee.getEmail().equals(txt_email))
                || txt_email.equals(email);
    }

    private boolean isPhoneUnique(List<Employee> employees, String txt_phone) {
        return employees.stream()
                .noneMatch(employee -> employee.getPhone() != null && employee.getPhone().equals(txt_phone));
    }

    private boolean isPhoneUniqueUpdate(List<Employee> employees, String txt_phone) {
        String phone = currentEmployee.getPhone();
        return employees.stream()
                .noneMatch(employee -> employee.getPhone() != null && employee.getPhone().equals(txt_phone))
                || txt_phone.equals(phone);
    }

    private boolean isCitizenIdUnique(List<Employee> employees, String txt_citizenId) {
        return employees.stream()
                .noneMatch(employee -> employee.getCitizenID() != null && employee.getCitizenID().equals(txt_citizenId));
    }

    private boolean isCitizenIdUniqueUpdate(List<Employee> employees, String txt_citizenId) {
        String citizenId = currentEmployee.getCitizenID();
        return employees.stream()
                .noneMatch(employee -> employee.getCitizenID() != null && employee.getCitizenID().equals(txt_citizenId))
                || txt_citizenId.equals(citizenId);
    }

    private boolean isPasswordStrong(String txt_password) {
        return txt_password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$%])[A-Za-z\\d@$%]{8,}$");
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
            isUploadNewImage = true;
        }
    }


    @FXML
    private String saveImage() {
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
                        return "defaultImg.jpg";
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
                return uniqueFileName;
            } catch (IOException e) {
                System.out.println("Lưu hình thất bại: " + e.getMessage());
            }
        } else {
            return "defaultImg.jpg";
        }

        return null;
    }

    private void setImage(String filename) {
        if (filename != null && !filename.isEmpty()) {
            String imagePath = "images/employee/" + filename;
            File file = new File(imagePath);

            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView_image.setImage(image);
                selectedImageFile = file;
            } else {
                // If the file doesn't exist, set a default image
                String defaultImagePath = "images/employee/defaultImg.jpg";
                Image defaultImage = new Image(new File(defaultImagePath).toURI().toString());
                imageView_image.setImage(defaultImage);
            }
        } else {
            // If the filename is null or empty, set a default image
            String defaultImagePath = "images/employee/defaultImg.jpg";
            Image defaultImage = new Image(new File(defaultImagePath).toURI().toString());
            imageView_image.setImage(defaultImage);
        }
    }

    @FXML
    void clearInput() {
        txt_name.clear();
        txt_address.clear();
        txt_phone.clear();
        txt_email.clear();
        txt_salary.clear();
        txt_citizenId.clear();
        txt_password.clear();
        txt_confirmPassword.clear();

        txt_dateOfBirth.clear();
        txt_dateOfHire.clear();

        cbb_department.selectFirst();
        cbb_position.selectFirst();

        setImage("defaultImg.jpg");

        clearValidateError();
    }
    private void clearValidateError() {
        lbl_error_name.setVisible(false);
        lbl_error_address.setVisible(false);
        lbl_error_phone.setVisible(false);
        lbl_error_email.setVisible(false);
        lbl_error_salary.setVisible(false);
        lbl_error_citizenId.setVisible(false);
        lbl_error_password.setVisible(false);

        lbl_error_dateOfBirth.setVisible(false);
        lbl_error_dateOfHire.setVisible(false);

        lbl_error_department.setVisible(false);
        lbl_error_position.setVisible(false);
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
    void saveButtonOnClick() {
        if (employeeValidator.validate()) {
            Employee employee = new Employee();
            employee.setName(txt_name.getText());
            employee.setAddress(txt_address.getText());
            employee.setPhone(txt_phone.getText());
            employee.setEmail(txt_email.getText());
            employee.setSalary(Double.parseDouble(txt_salary.getText()));
            employee.setCitizenID(txt_citizenId.getText());
            employee.setPassword(hashSHA256(txt_password.getText()));

            Department selectedDepartment = cbb_department.getSelectionModel().getSelectedItem();
            if (selectedDepartment != null) {
                employee.setDepartmentId(selectedDepartment.getId());
            }

            Position selectedPosition = cbb_position.getSelectionModel().getSelectedItem();
            if (selectedPosition != null) {
                employee.setPositionId(selectedPosition.getId());
            }

            employee.setImage(saveImage());

            employee.setDateOfBirth(txt_dateOfBirth.getValue());
            employee.setDateOfHire(txt_dateOfHire.getValue());


            employeeService.addEmployee(employee);

            if (employeeAddCallback != null) {
                employeeAddCallback.onEmployeeAdded(employee);
            }

            clearInput();

            // Show success message
            lbl_successMessage.setText("Add new employee successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 4 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();

            isUploadNewImage = false;
        }
    }

    void editEmployee(Employee employee) {
        txt_name.setText(employee.getName());
        txt_address.setText(employee.getAddress());
        txt_phone.setText(employee.getPhone());
        txt_email.setText(employee.getEmail());

        txt_salary.setText(String.format("%.0f", employee.getSalary()));

        txt_citizenId.setText(employee.getCitizenID());

        txt_dateOfBirth.setValue(employee.getDateOfBirth().minusDays(1)); // Trùng ngày thì nó không hiển thị, nên trừ 1 rồi set lại
        txt_dateOfBirth.setValue(employee.getDateOfBirth());

        txt_dateOfHire.setValue(employee.getDateOfHire().minusDays(1));
        txt_dateOfHire.setValue(employee.getDateOfHire());

        Department department = employee.getDepartment();
        cbb_department.getSelectionModel().selectItem(department);

        Position position = employee.getPosition();
        cbb_position.getSelectionModel().selectItem(position);

        setImage(employee.getImage());
    }

    @FXML
    void updateButtonOnClick() {
        if (employeeValidator.validate()) {

            currentEmployee.setName(txt_name.getText());
            currentEmployee.setAddress(txt_address.getText());
            currentEmployee.setPhone(txt_phone.getText());
            currentEmployee.setEmail(txt_email.getText());
            currentEmployee.setSalary(Double.parseDouble(txt_salary.getText()));
            currentEmployee.setCitizenID(txt_citizenId.getText());
            if (!txt_password.getText().isEmpty())
                currentEmployee.setPassword(hashSHA256(txt_password.getText()));

            Department selectedDepartment = cbb_department.getSelectionModel().getSelectedItem();
            if (selectedDepartment != null) {
                currentEmployee.setDepartmentId(selectedDepartment.getId());
                currentEmployee.setDepartment(selectedDepartment);
            }

            Position selectedPosition = cbb_position.getSelectionModel().getSelectedItem();
            if (selectedPosition != null) {
                currentEmployee.setPositionId(selectedPosition.getId());
                currentEmployee.setPosition(selectedPosition);
            }

            if (isUploadNewImage)
                currentEmployee.setImage(saveImage());

            currentEmployee.setDateOfBirth(txt_dateOfBirth.getValue());
            currentEmployee.setDateOfHire(txt_dateOfHire.getValue());

            employeeService.updateEmployee(currentEmployee);

            lbl_successMessage.setText("Update employee succesfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();

            int index = tableView.getItems().indexOf(currentEmployee);
            if (index >= 0) {
                tableView.getItems().set(index, currentEmployee);
                tableView.refresh();
            }

            isUploadNewImage = false;
        }
    }

    void updateMode() {
        isUpdate = true;
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(true);
    }

    void addMode() {
        isUpdate = false;
        hbox_addButtonGroup.setVisible(true);
        hbox_updateButtonGroup.setVisible(false);
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
}
