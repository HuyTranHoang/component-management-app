package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Order;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderAddController implements Initializable {
    private AnchorPane anchor_main_rightPanel;
    private AnchorPane orderView;
    private TableView<Order> tableView;
    //

    @FXML
    private HBox hbox_addButtonGroup;

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private Label lbl_error_deliveryDate;

    @FXML
    private Label lbl_error_monthOfWarranty;

    @FXML
    private Label lbl_error_note;

    @FXML
    private Label lbl_error_orderDate;

    @FXML
    private Label lbl_error_shipmentDate;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private MFXDatePicker txt_deliveryDate;

    @FXML
    private MFXDatePicker txt_orderDate;

    @FXML
    private MFXDatePicker txt_shipmentDate;

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    public void setOrderView(AnchorPane orderView) {
        this.orderView = orderView;
    }

    public void setTableView(TableView<Order> tableView) {
        this.tableView = tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txt_orderDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_orderDate.getLocale()));
        txt_deliveryDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_orderDate.getLocale()));
        txt_shipmentDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_orderDate.getLocale()));
    }
}
