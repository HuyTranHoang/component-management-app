package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderAddSelectEmployeeController implements Initializable {
    public interface EmployeeSelectionCallback {
        void onEmployeeSelected(Employee employee);
    }
    private EmployeeSelectionCallback employeeSelectionCallback;

    public void setEmployeeSelectionCallback(EmployeeSelectionCallback callback) {
        this.employeeSelectionCallback = callback;
    }

    //    List
    private ObservableList<Employee> employees;
    //  Pagination
    @FXML
    private Button firstPageButton;
    @FXML
    private Button lastPageButton;
    @FXML
    private Button nextButton;
    @FXML
    private HBox pageButtonContainer;
    @FXML
    private Button previousButton;
    private PaginationHelper<Employee> paginationHelper;

    // Customer Panel
    @FXML
    private TableView<Employee> tableView;
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
    private MFXTextField txt_employee_search;
    //Service
    EmployeeService employeeService = new EmployeeService();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employees = FXCollections.observableArrayList(employeeService.getAllEmployee());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(employees);
        paginationHelper.setTableView(tableView);

        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        paginationHelper.showFirstPage();

        initEnterKeyPressing();

        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                Employee employee = tableView.getSelectionModel().getSelectedItem();
                if (employeeSelectionCallback != null) {
                    employeeSelectionCallback.onEmployeeSelected(employee);
                    stage.close();
                }
            }
        });

    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tbc_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tbc_email.setCellValueFactory(new PropertyValueFactory<>("email"));

    }
    @FXML
    void showPreviousPage() {
        paginationHelper.showPreviousPage();
    }
    @FXML
    void showNextPage() {
        paginationHelper.showNextPage();
    }
    @FXML
    void showFirstPage() {
        paginationHelper.showFirstPage();
    }
    @FXML
    void showLastPage() {
        paginationHelper.showLastPage();
    }


    private void searchCustomerOnAction() {
        String searchText = txt_employee_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Employee> filter = employees.stream()
                    .filter(employee ->
                            employee.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    employee.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                                    employee.getPhone().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Employee> filterCustomers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterCustomers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(employees);
            paginationHelper.showFirstPage();
        }
    }

    private void initEnterKeyPressing() {
        txt_employee_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchCustomerOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_employee_search.clear();
                searchCustomerOnAction();
            }
        });
    }

    @FXML
    void resetFilterIconClicked() {
        txt_employee_search.setText("");
        searchCustomerOnAction();
    }
}
