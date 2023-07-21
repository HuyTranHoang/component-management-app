package vn.aptech.componentmanagementapp.controller.employee;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.controller.order.OrderFilterController;
import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.service.DepartmentService;
import vn.aptech.componentmanagementapp.service.PositionService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class EmployeeFilterController implements Initializable {

    public interface ViewResultCallback {
        void onViewResultClicked(List<Employee> filterEmployee);
    }

    private ViewResultCallback viewResultCallback;

    public void setViewResultCallback(ViewResultCallback callback) {
        this.viewResultCallback = callback;
    }

    public interface ClearFilterCallback {
        void onClearFilterClicked();
    }

    private ClearFilterCallback clearFilterCallback;

    public void setClearFilterCallback(ClearFilterCallback callback) {
        this.clearFilterCallback = callback;
    }


    @FXML
    private MFXToggleButton btn_toggleSalary;

    @FXML
    private MFXComboBox<String> cbb_bySalary;

    @FXML
    private MFXFilterComboBox<Department> cbb_department;

    @FXML
    private MFXFilterComboBox<Position> cbb_position;

    @FXML
    private Label lbl_error_salary;

    @FXML
    private MFXTextField txt_salaryAmount;

    //    Debound for search text field
    private Timer debounceTimer;
    private boolean isInputPending = false;
    private final long DEBOUNCE_DELAY = 1000; // Delay in milliseconds


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

    private Validator validator = new Validator();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBox();
        initValidator();
    }

    private void initComboBox() {
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

    private void initValidator() {
        validator.createCheck()
                .dependsOn("salaryAmount", txt_salaryAmount.textProperty())
                .withMethod(context -> {
                    String salaryAmount = context.get("salaryAmount");
                    if (btn_toggleSalary.isSelected())
                        if (salaryAmount.isEmpty())
                            context.error("Salary amount can't be empty");
                        else if(!salaryAmount.matches("\\d+"))
                            context.error("Salary can't contain characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_salary);
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
    void clearFilterButtonOnClick() {
        cbb_bySalary.selectFirst();
        cbb_department.selectFirst();
        cbb_position.selectFirst();

        btn_toggleSalary.setSelected(false);

        txt_salaryAmount.setText("0");

        filter_noti_label.setVisible(false);
        filter_noti_shape.setVisible(false);

        if (clearFilterCallback != null) {
            clearFilterCallback.onClearFilterClicked();
        }

        stage.close();
    }

    @FXML
    void viewResultButtonOnClick() {
        if (validator.validate()) {
            List<Employee> filterEmployee = employees;
            int countFilter = 0;

            if (btn_toggleSalary.isSelected()) {
                Double salaryAmount = Double.parseDouble(txt_salaryAmount.getText());
                String typeDate = cbb_bySalary.getSelectedItem();

                if (typeDate.equals("Above")) {
                    filterEmployee = filterEmployee.stream()
                            .filter(employee -> employee.getSalary() > salaryAmount)
                            .collect(Collectors.toList());
                } else {
                    filterEmployee = filterEmployee.stream()
                            .filter(employee -> employee.getSalary() < salaryAmount)
                            .collect(Collectors.toList());
                }
                countFilter++;
            }

            if (cbb_department.getSelectedIndex() != 0) {
                filterEmployee = filterEmployee.stream()
                        .filter(employee -> employee.getDepartment().getName().equals(cbb_department.getText()))
                        .collect(Collectors.toList());
                countFilter++;
            }

            if (cbb_position.getSelectedIndex() != 0) {
                filterEmployee = filterEmployee.stream()
                        .filter(employee -> employee.getPosition().getName().equals(cbb_position.getText()))
                        .collect(Collectors.toList());
                countFilter++;
            }

            // Search
            String searchText = txt_employee_search.getText().trim();
            if (!searchText.isEmpty()) {
                filterEmployee = filterEmployee.stream()
                        .filter(employee -> employee.getName().toLowerCase().contains(searchText.toLowerCase())
                                || employee.getEmail().toLowerCase().contains(searchText.toLowerCase())
                                || employee.getPhone().toLowerCase().contains(searchText.toLowerCase()))
                        .toList();
            }

            if (viewResultCallback != null) {
                viewResultCallback.onViewResultClicked(filterEmployee);
            }

            if (countFilter > 0 ) {
                filter_noti_shape.setVisible(true);
                filter_noti_label.setVisible(true);
                filter_noti_label.setText(String.valueOf(countFilter));
            } else {
                filter_noti_shape.setVisible(false);
                filter_noti_label.setVisible(false);
            }

            stage.close();
        }
    }

    public void initSearchListen() {
        txt_employee_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                viewResultButtonOnClick();
            else if (event.getCode() == KeyCode.ESCAPE) {
                txt_employee_search.clear();
                viewResultButtonOnClick();
            }
        });

        txt_employee_search.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear the previous timer if it exists
            if (debounceTimer != null) {
                debounceTimer.cancel();
            }

            // Set the input pending flag to true
            isInputPending = true;

            // Create a new timer
            debounceTimer = new Timer();
            debounceTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Check if there is a pending input
                    if (isInputPending) {
                        Platform.runLater(() -> {
                            // Call the viewResultButtonOnClick method
                            viewResultButtonOnClick();
                            // Set the input pending flag to false
                            isInputPending = false;
                        });
                    }
                }
            }, DEBOUNCE_DELAY);
        });
    }
}
