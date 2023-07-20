package vn.aptech.componentmanagementapp.controller.employee;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable, EmployeeAddController.EmployeeAddCallback {
    @FXML
    private MFXComboBox<String> cbb_orderBy;

    @FXML
    private MFXComboBox<String> cbb_sortBy;

    @FXML
    private AnchorPane employeeView;

    public void setEmployeeView(AnchorPane employeeView) {
        this.employeeView = employeeView;
    }

    @FXML
    private Label filter_noti_label;

    @FXML
    private Circle filter_noti_shape;

    @FXML
    private Button firstPageButton;

    @FXML
    private HBox hbox_addEditDelete;

    @FXML
    private HBox hbox_confirmDelete;

    @FXML
    private Button lastPageButton;

    @FXML
    private Button nextButton;

    @FXML
    private HBox pageButtonContainer;

    @FXML
    private Button previousButton;

    @FXML
    private TableView<Employee> tableView;
    @FXML
    private TableColumn<Employee, Boolean> tbc_checkbox;
    @FXML
    private TableColumn<Employee, Long> tbc_id;
    @FXML
    private TableColumn<Employee, String> tbc_name;
    @FXML
    private TableColumn<Employee, String> tbc_address;
    @FXML
    private TableColumn<Employee, String> tbc_phone;
    @FXML
    private TableColumn<Employee, String> tbc_email;
    @FXML
    private TableColumn<Employee, Double> tbc_salary;
    @FXML
    private TableColumn<Employee, String> tbc_department;
    @FXML
    private TableColumn<Employee, String> tbc_position;

    @FXML
    private MFXTextField txt_employee_search;

    // List
    private ObservableList<Employee> employees;
    private final ArrayList<Long> selectedEmployeeIds = new ArrayList<>();
    // Service
    private EmployeeService employeeService = new EmployeeService();

    // Cached views
    private AnchorPane addEmployeeView;
    private EmployeeAddController employeeAddController;

    private PaginationHelper<Employee> paginationHelper;

    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paginationHelper = new PaginationHelper<>();
        initTableView();

        paginationHelper.setTableView(tableView);
        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        List<Employee> employeeList = employeeService.getAllEmployee();
        employees = FXCollections.observableArrayList(employeeList);

        paginationHelper.setItems(employees);
        paginationHelper.showFirstPage();
//        initFilterStage();
//        filterController.initSearchListen();
//        initTableViewEvent();
//        initSort();
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tbc_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tbc_email.setCellValueFactory(new PropertyValueFactory<>("email"));
//        tbc_salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        tbc_salary.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_salary.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSalary()));

        tbc_department.setCellValueFactory(cellData -> {
            Department department = cellData.getValue().getDepartment();
            if (department != null) {
                return new ReadOnlyObjectWrapper<>(department.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tbc_position.setCellValueFactory(cellData -> {
            Position position = cellData.getValue().getPosition();
            if (position != null) {
                return new ReadOnlyObjectWrapper<>(position.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        initCheckBox();
    }

    private void initCheckBox() {
        tbc_checkbox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Employee, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Employee employee = getTableRow().getItem();
                    boolean selected = checkBox.isSelected();
                    employee.setSelected(selected);
                    if (selected) {
                        selectedEmployeeIds.add(employee.getId());
                    } else {
                        selectedEmployeeIds.remove(employee.getId());
                    }
                    updateRowStyle();
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                    updateRowStyle();
                }
            }

            private void updateRowStyle() {
                boolean selected = checkBox.isSelected();
                TableRow<Employee> currentRow = getTableRow();
                if (currentRow != null) {
                    currentRow.setStyle(selected ? "-fx-background-color: #ffb8b4;" : "");
                }
            }
        });

    }

    @FXML
    void addButtonOnClick() {
        if (addEmployeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/employee/employee-add.fxml"));
                addEmployeeView = fxmlLoader.load();
                employeeAddController = fxmlLoader.getController();
                employeeAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                employeeAddController.setEmployeeView(employeeView);
                employeeAddController.setTableView(tableView);
                employeeAddController.setEmployees(employees);

                employeeAddController.setEmployeeAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        employeeAddController.clearInput();
        employeeAddController.addMode();

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(addEmployeeView);
    }

    private void uncheckAllCheckboxes() {
        for (Employee employee : tableView.getItems()) {
            employee.setSelected(false);
        }
        selectedEmployeeIds.clear();
    }


    @FXML
    void backButtonOnClick() {
        hbox_addEditDelete.setVisible(true);
        hbox_confirmDelete.setVisible(false);

        tbc_checkbox.setVisible(false);

        uncheckAllCheckboxes();
        tableView.refresh();
    }

    @FXML
    void deleteButtonOnClick() {
        hbox_addEditDelete.setVisible(false);
        hbox_confirmDelete.setVisible(true);

        tbc_checkbox.setVisible(true);
    }

    @FXML
    void deleteContextOnClick() {

    }

    @FXML
    void deleteSelectedProductOnClick() {

    }

    @FXML
    void editButtonOnClick() {
        employeeAddController.updateMode();
    }

    @FXML
    void filterButtonOnClick() {

    }

    @FXML
    void resetFilterIconClicked() {

    }

    @FXML
    void showFirstPage() {
        paginationHelper.showFirstPage();
    }

    @FXML
    void showLastPage() {
        paginationHelper.showLastPage();
    }

    @FXML
    void showNextPage() {
        paginationHelper.showNextPage();
    }

    @FXML
    void showPreviousPage() {
        paginationHelper.showPreviousPage();
    }

    @Override
    public void onEmployeeAdded(Employee employee) {
        employees.add(employee);
        showLastPage();
    }
}
