package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeSalaryController implements Initializable{
    @FXML
    private MFXComboBox<String> cbb_orderBy;

    @FXML
    private Button firstPageButton;

    @FXML
    private Button lastPageButton;

    @FXML
    private Label lbl_averageEmployeeSalary;

    @FXML
    private Label lbl_totalEmployee;

    @FXML
    private Label lbl_totalEmployeeSalary;

    @FXML
    private Button nextButton;

    @FXML
    private HBox pageButtonContainer;

    @FXML
    private Button previousButton;

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, Integer> tbc_department;

    @FXML
    private TableColumn<Employee, String> tbc_name;

    @FXML
    private TableColumn<Employee, Integer> tbc_position;

    @FXML
    private TableColumn<Employee, Double> tbc_salary;

    private PaginationHelper<Employee> paginationHelper;

    private EmployeeService employeeService = new EmployeeService();

    private ObservableList<Employee> employees;

    private DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");


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

        initComboBox();
        initSort();
    }

    private void initTableView() {
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        tbc_department.setCellValueFactory(new PropertyValueFactory<>("department"));

        tbc_salary.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_salary.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSalary()));
    }

    private void initComboBox() {
        List<String> employeeList = List.of("ASC", "DESC");
        cbb_orderBy.setItems(FXCollections.observableArrayList(employeeList));
        cbb_orderBy.selectFirst();
    }

    private void initSort() {
        cbb_orderBy.setItems(FXCollections.observableArrayList(List.of("ASC", "DESC")));
        // Add listeners to both ComboBoxes
        cbb_orderBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
    }

    private void applySorting() {
        String orderBy = cbb_orderBy.getValue();
        Comparator<Employee> comparator = Comparator.comparing(Employee::getSalary);
        if ("DESC".equals(orderBy)) {
            comparator = comparator.reversed();
        }

        FXCollections.sort(employees, comparator);
        showFirstPage();
    }

    @FXML
    public void reloadEmployee() {
        employees = FXCollections.observableArrayList(employeeService.getAllEmployee());
        paginationHelper.setItems(employees);
        applySorting();

        double sumSalary = 0;
        for (Employee employee: employees) {
            sumSalary += employee.getSalary();
        }
        double averageSalary = sumSalary/employees.size();

        lbl_totalEmployee.setText(String.valueOf(employees.size()));
        lbl_totalEmployeeSalary.setText(decimalFormat.format(sumSalary));
        lbl_averageEmployeeSalary.setText(decimalFormat.format(averageSalary));
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
}
