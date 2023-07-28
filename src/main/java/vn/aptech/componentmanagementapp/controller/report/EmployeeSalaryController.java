package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.Collections;
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

    private DecimalFormat decimalFormat = new DecimalFormat();


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
        initComboBoxEvent();
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

    private void initComboBoxEvent() {
        EventHandler<ActionEvent> comboBoxEventHandler = event -> {
            String employee = cbb_orderBy.getValue();
            if (employee == null) {
                return;
            }

            if (employee.equals("DESC"))
                Collections.reverse(employees);

            double sumTotal = 0;
            for (Employee employee1: employees) {
                sumTotal = sumTotal + employee1.getSalary();
            }

            double averageSalary = 0;
            for (Employee employee1: employees) {
                averageSalary = sumTotal/employeeService.getAllEmployee().size();
            }

            lbl_totalEmployee.setText(String.valueOf(employeeService.getAllEmployee().size()));
            lbl_totalEmployeeSalary.setText(decimalFormat.format(sumTotal));
            lbl_averageEmployeeSalary.setText(decimalFormat.format(averageSalary));

            paginationHelper.setItems(employees);
            showFirstPage();
        };

        cbb_orderBy.setOnAction(comboBoxEventHandler);
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
