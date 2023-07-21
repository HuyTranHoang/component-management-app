package vn.aptech.componentmanagementapp.controller.employee;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.service.DepartmentService;
import vn.aptech.componentmanagementapp.service.PositionService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeFilterController implements Initializable {

    @FXML
    private MFXToggleButton btn_toggleSalary;

    @FXML
    private MFXComboBox<String> cbb_bySalary;

    @FXML
    private MFXFilterComboBox<Department> cbb_department;

    @FXML
    private MFXFilterComboBox<Position> cbb_position;

    @FXML
    private Label lbl_customerName;

    @FXML
    private Label lbl_customerPhone;

    @FXML
    private Label lbl_employeeName;

    @FXML
    private Label lbl_employeePhone;

    @FXML
    private Label lbl_error_from;

    @FXML
    private MFXTextField txt_salaryAmount;

    @FXML
    private VBox vbox_customerInfo;

    @FXML
    private VBox vbox_employeeInfo;

    // List
    private ObservableList<Employee> employees;
    private ObservableList<Department> departments;
    private ObservableList<Position> positions;

    // Service
    DepartmentService departmentService = new DepartmentService();
    PositionService positionService = new PositionService();

    // Truyền từ ngoài vào

    public void setEmployees(ObservableList<Employee> employees) {
        this.employees = employees;
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Label filter_noti_label; // Truyền vào OrderFilterController để set visiable và text
    private Circle filter_noti_shape;

    public void setFilter_noti_label(Label filter_noti_label) {
        this.filter_noti_label = filter_noti_label;
    }

    public void setFilter_noti_shape(Circle filter_noti_shape) {
        this.filter_noti_shape = filter_noti_shape;
    }

    private MFXTextField txt_employee_search;

    public void setTxt_employee_search(MFXTextField txt_employee_search) {
        this.txt_employee_search = txt_employee_search;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> bySalary =
                FXCollections.observableArrayList(List.of("Above", "Below"));
        cbb_bySalary.setItems(bySalary);
        cbb_bySalary.selectFirst();

        departments = FXCollections.observableArrayList(departmentService.getAllDepartment());
        Department department = new Department();
        department.setName("All");
        departments.add(0, department);
        cbb_department.setItems(departments);
        cbb_department.selectFirst();

        positions = FXCollections.observableArrayList(positionService.getAllPosition());
        Position position = new Position();
        position.setName("All");
        positions.add(0, position);
        cbb_position.setItems(positions);
        cbb_position.selectFirst();

    }

    @FXML
    void clearFilterButtonOnClick(ActionEvent event) {

    }

    @FXML
    void viewResultButtonOnClick(ActionEvent event) {

    }

}
