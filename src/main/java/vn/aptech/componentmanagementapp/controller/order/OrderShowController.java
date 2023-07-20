package vn.aptech.componentmanagementapp.controller.order;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.service.OrderDetailService;
import vn.aptech.componentmanagementapp.util.ProductInfoView;
import vn.aptech.componentmanagementapp.util.ProductInfoViewShow;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderShowController implements Initializable {

    private AnchorPane anchor_main_rightPanel;
    private AnchorPane orderView;

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    public void setOrderView(AnchorPane orderView) {
        this.orderView = orderView;
    }

    private Order currentOrder;

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    private List<OrderDetail> orderDetails = new ArrayList<>();

    //  Service
    OrderDetailService orderDetailService = new OrderDetailService();

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private Label lbl_EmployeeName;

    @FXML
    private Label lbl_customerName;

    @FXML
    private Label lbl_customerPhone;

    @FXML
    private Label lbl_deliveryDate;

    @FXML
    private Label lbl_deliveryLocation;

    @FXML
    private Label lbl_note;

    @FXML
    private Label lbl_orderDate;

    @FXML
    private Label lbl_receiveDate;

    @FXML
    private Label lbl_totalAmount;

    @FXML
    private VBox vbox_orderDetail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void setInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

        Customer currentCustomer = currentOrder.getCustomer();
        Employee currentEmployee = currentOrder.getEmployee();

        lbl_orderDate.setText(formatter.format(currentOrder.getOrderDate()));
        lbl_deliveryDate.setText(formatter.format(currentOrder.getDeliveryDate()));
        lbl_receiveDate.setText(formatter.format(currentOrder.getReceiveDate()));

        lbl_customerName.setText(currentCustomer.getName());
        lbl_customerPhone.setText(currentCustomer.getPhone());

        lbl_EmployeeName.setText(currentEmployee.getName());

        lbl_deliveryLocation.setText(currentOrder.getDeliveryLocation());
        lbl_note.setText(currentOrder.getNote());

        clearOrderDetail();
        orderDetails = orderDetailService.getAllOrderDetailByOrderId(currentOrder.getId());
        double totalAmount = 0;

        for (OrderDetail orderDetail: orderDetails) {
            ProductInfoViewShow productInfoView = new ProductInfoViewShow();
            productInfoView.getLblProductName().setText(orderDetail.getName());
            productInfoView.getLblProductPrice().setText(decimalFormat.format(orderDetail.getPrice()));
            productInfoView.getLblProductDiscount().setText(String.valueOf(orderDetail.getDiscount()));
            productInfoView.getLblProductQuantity().setText(String.valueOf(orderDetail.getQuantity()));
            productInfoView.getLblProductTotalAmount().setText(decimalFormat.format(orderDetail.getTotalAmount()));
            productInfoView.setVbox_orderDetail(vbox_orderDetail);
            productInfoView.setOrderDetail(orderDetail);
            productInfoView.setOrderDetails(orderDetails);

            totalAmount += orderDetail.getTotalAmount();

            vbox_orderDetail.getChildren().add(productInfoView);
        }
        lbl_totalAmount.setText(decimalFormat.format(totalAmount));
    }

    @FXML
    void clearOrderDetail() {
        vbox_orderDetail.getChildren().clear();
        orderDetails.clear();
        lbl_totalAmount.setText("");
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
}
