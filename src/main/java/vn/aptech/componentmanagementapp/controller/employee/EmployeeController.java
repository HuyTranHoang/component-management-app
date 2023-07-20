package vn.aptech.componentmanagementapp.controller.employee;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import vn.aptech.componentmanagementapp.model.Employee;

public class EmployeeController {
    @FXML
    private MFXComboBox<?> cbb_orderBy;

    @FXML
    private MFXComboBox<?> cbb_sortBy;

    @FXML
    private AnchorPane employeeView;

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

    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @FXML
    void addButtonOnClick() {

    }

    @FXML
    void backButtonOnClick() {

    }

    @FXML
    void deleteButtonOnClick() {

    }

    @FXML
    void deleteContextOnClick() {

    }

    @FXML
    void deleteSelectedProductOnClick() {

    }

    @FXML
    void editButtonOnClick() {

    }

    @FXML
    void filterButtonOnClick() {

    }

    @FXML
    void resetFilterIconClicked() {

    }

    @FXML
    void showFirstPage() {

    }

    @FXML
    void showLastPage() {

    }

    @FXML
    void showNextPage() {

    }

    @FXML
    void showPreviousPage() {

    }
}
