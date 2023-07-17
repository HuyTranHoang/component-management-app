package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.SupplierService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    //    List
    private ObservableList<Supplier> suppliers;

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
    private PaginationHelper<Supplier> paginationHelper;


    @FXML
    private AnchorPane anchor_product_view;
    //Service
    SupplierService supplierService = new SupplierService();


    //Validate
    @FXML
    private Label lbl_error_customerAddress;

    @FXML
    private Label lbl_error_customerEmail;

    @FXML
    private Label lbl_error_customerName;

    @FXML
    private Label lbl_error_customerPhone;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private Label lbl_text;


    // Customer Panel
    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> tbc_address;

    @FXML
    private TableColumn<?, ?> tbc_checkbox;

    @FXML
    private TableColumn<?, ?> tbc_email;

    @FXML
    private TableColumn<?, ?> tbc_id;

    @FXML
    private TableColumn<?, ?> tbc_name;

    @FXML
    private TableColumn<?, ?> tbc_phone;

    @FXML
    private MFXTextField txt_address;

    @FXML
    private MFXTextField txt_email;

    @FXML
    private MFXTextField txt_name;

    @FXML
    private MFXTextField txt_phone;

    @FXML
    private MFXTextField txt_supplier_search;

    @FXML
    private HBox hbox_addEditDelete;

    @FXML
    private HBox hbox_addGroup;

    @FXML
    private HBox hbox_confirmDelete;

    @FXML
    private HBox hbox_updateGroup;

    @FXML
    private MFXButton btn_back;

    @FXML
    private MFXButton btn_clear;

    @FXML
    private MFXButton btn_store;

    @FXML
    private MFXButton btn_update;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    void addButtonOnClick(ActionEvent event) {

    }

    @FXML
    void backButtonOnClick(ActionEvent event) {

    }

    @FXML
    void clearButtonOnClick(ActionEvent event) {

    }

    @FXML
    void deleteButtonOnClick(MouseEvent event) {

    }

    @FXML
    void deleteContextOnClick(ActionEvent event) {

    }

    @FXML
    void deleteSelectedCustomerOnClick(ActionEvent event) {

    }

    @FXML
    void editButtonOnClick(ActionEvent event) {

    }

    @FXML
    void resetFilterIconClicked(MouseEvent event) {

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

    @FXML
    void storeButtonOnClick(ActionEvent event) {

    }

    @FXML
    void updateButtonOnClick(ActionEvent event) {

    }

}

