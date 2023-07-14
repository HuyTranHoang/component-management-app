package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.OrderService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class OrderAddController implements Initializable {

    // Call back add order
    public interface OrderAddCallback {
        void onOrderAdded(Order order);
    }
    private OrderAddCallback orderAddCallback;
    public void setOrderAddCallback(OrderAddCallback orderAddCallback) {
        this.orderAddCallback = orderAddCallback;
    }


    private AnchorPane anchor_main_rightPanel;
    private AnchorPane orderView;

    private ObservableList<Order> orders;
    private TableView<Order> tableView;
    private Order currentOrder;

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    @FXML
    private HBox hbox_addButtonGroup;

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private Label lbl_error_customerId;

    @FXML
    private Label lbl_error_deliveryDate;

    @FXML
    private Label lbl_error_deliveryLocation;

    @FXML
    private Label lbl_error_employeeId;

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

    @FXML
    private MFXTextField txt_customerId;

    @FXML
    private MFXTextField txt_deliveryLocation;

    @FXML
    private MFXTextField txt_employeeId;

    @FXML
    private MFXTextField txt_note;

    // Service
    private final OrderService orderService = new OrderService();

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


    @FXML
    void listOrderButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-order.fxml"));
                orderView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }


    @FXML
    void storeButtonOnClick(){
        //TODO : Làm validate
        if(true){
            Order order = new Order();
            order.setOrderDate(txt_orderDate.getValue().atTime(LocalTime.now()));
            order.setDeliveryDate(txt_deliveryDate.getValue().atTime(LocalTime.now()));
            order.setShipmentDate(txt_shipmentDate.getValue().atTime(LocalTime.now()));
            order.setDeliveryLocation(txt_deliveryLocation.getText());
            order.setCustomerId(Long.parseLong(txt_customerId.getText()));
            order.setEmployeeId(Long.parseLong(txt_employeeId.getText()));
            order.setNote(txt_note.getText());

            orderService.addOrder(order);

            // Pass the newly added product to the callback
            if (orderAddCallback != null) {
                orderAddCallback.onOrderAdded(order);
            }

            // Show success message
            lbl_successMessage.setText("Add new order successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 4 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();
        }
    }

    void updateMode() {
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(true);
    }

    void addMode() {
        hbox_addButtonGroup.setVisible(true);
        hbox_updateButtonGroup.setVisible(false);
    }

    void editOrder(Order order) {
        txt_orderDate.setValue(order.getOrderDate().toLocalDate());
        txt_deliveryDate.setValue(order.getDeliveryDate().toLocalDate());
        txt_shipmentDate.setValue(order.getShipmentDate().toLocalDate());

        txt_customerId.setText(String.valueOf(order.getCustomerId()));
        txt_employeeId.setText(String.valueOf(order.getEmployeeId()));

        txt_deliveryLocation.setText(order.getDeliveryLocation());
        txt_note.setText(order.getNote());
    }

    @FXML
    void updateButtonOnClick() {
        //TODO : Làm validate
        if (true) {
            currentOrder.setOrderDate(txt_orderDate.getValue().atTime(LocalTime.now()));
            currentOrder.setDeliveryDate(txt_deliveryDate.getValue().atTime(LocalTime.now()));
            currentOrder.setShipmentDate(txt_shipmentDate.getValue().atTime(LocalTime.now()));
            currentOrder.setDeliveryLocation(txt_deliveryLocation.getText());
            currentOrder.setCustomerId(Long.parseLong(txt_customerId.getText()));
            currentOrder.setEmployeeId(Long.parseLong(txt_employeeId.getText()));
            currentOrder.setNote(txt_note.getText());

            orderService.updateOrder(currentOrder);

            // Show success message
            lbl_successMessage.setText("Update product succesfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();

            int index = tableView.getItems().indexOf(currentOrder);
            System.out.println(index);
            if (index >= 0) {
                tableView.getItems().set(index, currentOrder);
            }

        }
    }

    @FXML
    void clearInput() { // Được gọi trước khi vào add view ở Order Add Controller
        txt_orderDate.clear();
        txt_deliveryDate.clear();
        txt_shipmentDate.clear();
        txt_customerId.clear();
        txt_employeeId.clear();
        txt_deliveryLocation.clear();
        txt_note.clear();

        clearValidateError();
    }

    private void clearValidateError() {
        lbl_error_orderDate.setVisible(false);
        lbl_error_deliveryDate.setVisible(false);
        lbl_error_shipmentDate.setVisible(false);
        lbl_error_customerId.setVisible(false);
        lbl_error_employeeId.setVisible(false);
        lbl_error_deliveryLocation.setVisible(false);
        lbl_error_note.setVisible(false);
    }
}
