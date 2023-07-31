package vn.aptech.componentmanagementapp.controller.employee;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;
import vn.aptech.componentmanagementapp.util.SetImageAlert;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeController implements Initializable, EmployeeAddController.EmployeeAddCallback,
        EmployeeFilterController.ViewResultCallback, EmployeeFilterController.ClearFilterCallback {
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
    private TableColumn<Employee, Department> tbc_department;
    @FXML
    private TableColumn<Employee, String> tbc_position;

    @FXML
    private MFXTextField txt_employee_search;

    @FXML
    private HBox hbox_noti;
    private Timeline timeline;

    // List
    private ObservableList<Employee> employees;
    private final ArrayList<Long> selectedEmployeeIds = new ArrayList<>();
    // Service
    private EmployeeService employeeService = new EmployeeService();

    //    Filter Panel
    private Scene filterScene;
    private Stage filterStage;

    @FXML
    private Label filter_noti_label; // Truyền vào OrderFilterController để set visiable và text
    @FXML
    private Circle filter_noti_shape;

    // Cached views
    private AnchorPane addEmployeeView;

    //    Controller to call clear filter function in this
    private EmployeeFilterController filterController;
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

        initFilterStage();
        filterController.initSearchListen();
        initTableViewEvent();
        initSort();
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

        tbc_department.setCellValueFactory(new PropertyValueFactory<>("department"));
        tbc_position.setCellValueFactory(new PropertyValueFactory<>("position"));

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

    private void initTableViewEvent() {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                editButtonOnClick();
            }
        });
    }

    private void initSort() {
        cbb_sortBy.setItems(FXCollections.observableArrayList(List.of("Id", "Name", "Salary", "Department", "Position")));
        cbb_orderBy.setItems(FXCollections.observableArrayList(List.of("ASC", "DESC")));
        // Add listeners to both ComboBoxes
        cbb_sortBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
        cbb_orderBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
    }

    private void applySorting() {
        String sortBy = cbb_sortBy.getValue();
        String orderBy = cbb_orderBy.getValue();
        Comparator<Employee> comparator = switch (sortBy) {
            case "Name" -> Comparator.comparing(Employee::getName);
            case "Salary" -> Comparator.comparing(Employee::getSalary);
            case "Department" -> Comparator.comparing(employee -> employee.getDepartment().getName());
            case "Position" -> Comparator.comparing(employee -> employee.getPosition().getName());
            default -> Comparator.comparing(Employee::getId);
        };
        // Check the selected value of cbb_orderBy and adjust the comparator accordingly
        if ("DESC".equals(orderBy)) {
            comparator = comparator.reversed();
        }
        // Sort the products list with the chosen comparator
        FXCollections.sort(employees, comparator);
        showFirstPage();
    }


    @FXML
    void addButtonOnClick() {
        if (addEmployeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/employee/employee-add.fxml"));
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
        employeeAddController.setRequestFocus();
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
        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Please select a employee before deleting!");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected employee? " +
                    "If you delete, all order belong to that employee also get deleted.");

            SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                employeeService.deleteEmployee(selectedEmployee.getId());
                employees.remove(selectedEmployee);
                tableView.getItems().remove(selectedEmployee); // Remove the product from the TableView
                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();
                hbox_noti.setVisible(true);
                new FadeInRight(hbox_noti).play();
                timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
                timeline.play();
            }
        }
    }

    @FXML
    void deleteSelectedEmployeeOnClick() {
        if (selectedEmployeeIds.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Confirm");
            error.setHeaderText(null);
            error.setContentText("Please select checkbox employee you want to delete.");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete " + selectedEmployeeIds.size() + " employees? " +
                    "If you delete, all orders belong to this employee also get deleted.");

            SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                selectedEmployeeIds.forEach(aLong -> {
                    employeeService.deleteEmployee(aLong);
                    Employee employee = employees.stream()
                            .filter(p -> p.getId() == aLong)
                            .findFirst()
                            .orElse(null);

                    employees.remove(employee);
                    paginationHelper.getPageItems().remove(employee);
                });

                hbox_noti.setVisible(true);
                new FadeInRight(hbox_noti).play();
                timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
                timeline.play();
                uncheckAllCheckboxes();
                paginationHelper.showCurrentPage();
                tableView.refresh();
            }
        }

    }

    @FXML
    void editButtonOnClick() {
        if (addEmployeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/employee/employee-add.fxml"));
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

        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Please select employee before edit!");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {
            employeeAddController.clearInput();
            employeeAddController.updateMode();
            employeeAddController.editEmployee(selectedEmployee);
            employeeAddController.setCurrentEmployee(selectedEmployee);

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(addEmployeeView);
        }
    }

    private void initFilterStage() {
        try {
            if (filterScene == null && filterStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/employee/employee-filter.fxml"));
                filterScene = new Scene(fxmlLoader.load());
                filterStage = new Stage();
                filterStage.setTitle("Filter Employee");
                filterStage.initModality(Modality.APPLICATION_MODAL);

                filterController = fxmlLoader.getController();
                filterController.setEmployees(employees);

                // Xử lý dữ liệu sau khi viewResultButtonOnClick() được gọi và nhận filterOrder
                // ... thực hiện các thao tác khác với filterOrder ...
//                filterController.setViewResultCallback(filterOrder -> {
//                    paginationHelper.setItems(FXCollections.observableArrayList(filterOrder));
//                    paginationHelper.showFirstPage();
//                });

                Image image = null;

                URL resourceURL = ComponentManagementApplication.class.getResource("images/employee.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }

                filterStage.getIcons().add(image);

                filterStage.initModality(Modality.APPLICATION_MODAL);

                filterController.setStage(filterStage);
                filterController.setViewResultCallback(this);
                filterController.setClearFilterCallback(this);
                filterController.setFilter_noti_label(filter_noti_label);
                filterController.setFilter_noti_shape(filter_noti_shape);
                filterController.setTxt_employee_search(txt_employee_search);

                filterStage.setScene(filterScene);
                filterStage.setResizable(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void filterButtonOnClick() {
        filterStage.show();
    }

    @FXML
    public void resetFilterIconClicked() {
        if (filterController != null) {
            filterController.clearFilterButtonOnClick();
        }

        cbb_sortBy.selectFirst();
        cbb_orderBy.selectFirst();

        employees = FXCollections.observableArrayList(employeeService.getAllEmployee());
        paginationHelper.setItems(employees);

        uncheckAllCheckboxes();

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

    @Override
    public void onViewResultClicked(List<Employee> filterEmployee) {
        paginationHelper.setItems(FXCollections.observableArrayList(filterEmployee));
        showFirstPage();
    }

    public void reloadEmployee() {
        employees = FXCollections.observableArrayList(employeeService.getAllEmployee());
        paginationHelper.setItems(employees);
//        showFirstPage();
        paginationHelper.showCurrentPage();
    }

    @Override
    public void onClearFilterClicked() {
        employees = FXCollections.observableArrayList(employeeService.getAllEmployee());
        paginationHelper.setItems(employees);
        showFirstPage();

        uncheckAllCheckboxes();
    }

    @FXML
    void hideNoti() {
        timeline.stop();
        new FadeOutRight(hbox_noti).play();
    }
}
