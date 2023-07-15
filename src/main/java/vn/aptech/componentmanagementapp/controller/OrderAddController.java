package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.service.OrderDetailService;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.util.ProductInfoView;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderAddController implements Initializable, OrderAddSelectCustomerController.CustomerSelectionCallback {

    @Override
    public void onCustomerSelected(Customer customer) {
        currentCustomer = customer;
        txt_customerPhone.setText(customer.getPhone());
        txt_customerName.setText(customer.getName());
    }

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
    private Label lbl_error_note;

    @FXML
    private Label lbl_error_orderDate;

    @FXML
    private Label lbl_error_shipmentDate;

    @FXML
    private Label lbl_error_orderDetailEmpty;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private Label lbl_totalAmount;

    @FXML
    private MFXDatePicker txt_deliveryDate;

    @FXML
    private MFXDatePicker txt_orderDate;

    @FXML
    private MFXDatePicker txt_receiveDate;

    @FXML
    private MFXTextField txt_customerName;

    @FXML
    private MFXTextField txt_customerPhone;

    @FXML
    private MFXTextField txt_deliveryLocation;

    @FXML
    private MFXTextField txt_employeeName;

    @FXML
    private MFXTextField txt_note;

    //  Validator
    private final Validator orderAddValidator = new Validator();


    // Service
    private final OrderService orderService = new OrderService();
    private final OrderDetailService orderDetailService = new OrderDetailService();

    // Cache order details
    private Scene orderDetailScene;
    private Stage orderDetailStage;

    private Scene selectCustomerScene;
    private Stage selectCustomerStage;

    private Customer currentCustomer;
    private Employee currentEmployee;

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    @FXML
    private VBox vbox_orderDetail;
    // List order details
    private List<OrderDetail> orderDetails = new ArrayList<>();

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
        txt_receiveDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_orderDate.getLocale()));

        initValidator();
        initEnterKeyPressing();

        Platform.runLater(() -> txt_employeeName.setText(currentEmployee.getName()));
    }

    public void setTextEmployeeName(Employee employee) {
        txt_employeeName.setText(employee.getName());
    }

    private void initValidator() {
        orderAddValidator.createCheck()
                .dependsOn("orderDate", txt_orderDate.valueProperty())
                .withMethod(context -> {
                    LocalDate orderDate = context.get("orderDate");
                    if (orderDate == null) {
                        context.error("Order date can't be empty");
                    } else if (orderDate.isBefore(LocalDate.now())) {
                        context.error("Order date can't be in the past");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_orderDate);

        orderAddValidator.createCheck()
                .dependsOn("deliveryDate", txt_deliveryDate.valueProperty())
                .dependsOn("orderDate", txt_orderDate.valueProperty())
                .withMethod(context -> {
                    LocalDate deliveryDate = context.get("deliveryDate");
                    LocalDate orderDate = context.get("orderDate");
                    if (deliveryDate == null) {
                        context.error("Delivery date can't be empty");
                    } else if (deliveryDate.isBefore(LocalDate.now())) {
                        context.error("Delivery date can't be in the past");
                    } else if (deliveryDate.isBefore(orderDate)) {
                        context.error("Delivery date can't be before order date");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_deliveryDate);

        orderAddValidator.createCheck()
                .dependsOn("shipmentDate", txt_receiveDate.valueProperty())
                .dependsOn("deliveryDate", txt_deliveryDate.valueProperty())
                .dependsOn("orderDate", txt_orderDate.valueProperty())
                .withMethod(context -> {
                    LocalDate shipmentDate = context.get("shipmentDate");
                    LocalDate deliveryDate = context.get("deliveryDate");
                    LocalDate orderDate = context.get("orderDate");
                    if (shipmentDate == null) {
                        context.error("Shipment date can't be empty");
                    } else if (shipmentDate.isBefore(LocalDate.now())) {
                        context.error("Shipment date can't be in the past");
                    } else if (shipmentDate.isBefore(deliveryDate)) {
                        context.error("Shipment date can't be before delivery date");
                    } else if (shipmentDate.isBefore(orderDate)) {
                        context.error("Shipment date can't be before order date");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_shipmentDate);

        orderAddValidator.createCheck()
                .dependsOn("deliveryLocation", txt_deliveryLocation.textProperty())
                .withMethod(context -> {
                    String deliveryLocation = context.get("deliveryLocation");
                    if (deliveryLocation.isEmpty())
                        context.error("Delivery location can't be empty");
                    else if (deliveryLocation.length() > 255)
                        context.error("Delivery location length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_deliveryLocation);

        orderAddValidator.createCheck()
                .withMethod(context -> {
                    if (orderDetails.isEmpty())
                        context.error("Order details can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_orderDetailEmpty);
    }

    private Decoration labelDecorator(ValidationMessage message) {
        return new Decoration() {
            @Override
            public void add(Node target) {
                ((Label) target).setText(message.getText());
                target.setVisible(true);
            }

            @Override
            public void remove(Node target) {
                target.setVisible(false);
            }
        };
    }

    private void initEnterKeyPressing() {
        EventHandler<KeyEvent > storeOrUpdateEventHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (hbox_addButtonGroup.visibleProperty().get()) {
                    storeButtonOnClick();
                } else {
                    updateButtonOnClick();
                }
            } else if (event.getCode() == KeyCode.ESCAPE)
                listOrderButtonOnClick();
        };

       txt_deliveryLocation.setOnKeyPressed(storeOrUpdateEventHandler);
       txt_note.setOnKeyPressed(storeOrUpdateEventHandler);
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
        if(orderAddValidator.validate()){
            Order order = new Order();
            order.setOrderDate(txt_orderDate.getValue().atTime(LocalTime.now()));
            order.setDeliveryDate(txt_deliveryDate.getValue().atTime(LocalTime.now()));
            order.setReceiveDate(txt_receiveDate.getValue().atTime(LocalTime.now()));
            order.setDeliveryLocation(txt_deliveryLocation.getText());
            order.setCustomerId(currentCustomer.getId());
            order.setEmployeeId(currentEmployee.getId());
            order.setNote(txt_note.getText());

            long orderId = orderService.addOrderReturnId(order);
            orderDetails.forEach(orderDetail -> {
                orderDetail.setOrderId(orderId);
                orderDetailService.addOrderDetail(orderDetail);
            });

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
         DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

        txt_orderDate.setValue(order.getOrderDate().toLocalDate());
        txt_deliveryDate.setValue(order.getDeliveryDate().toLocalDate());
        txt_receiveDate.setValue(order.getReceiveDate().toLocalDate());

//        txt_customerId.setText(String.valueOf(order.getCustomerId()));
//        txt_employeeId.setText(String.valueOf(order.getEmployeeId()));
        txt_customerPhone.setText(currentCustomer.getPhone());
        txt_customerName.setText(currentCustomer.getName());
        txt_employeeName.setText(currentEmployee.getName());

        txt_deliveryLocation.setText(order.getDeliveryLocation());
        txt_note.setText(order.getNote());


        clearOrderDetail();
        orderDetails = orderDetailService.getAllOrderDetailByOrderId(order.getId());
        double totalAmount = 0;

        for (OrderDetail orderDetail: orderDetails) {
            ProductInfoView productInfoView = new ProductInfoView();
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
    void updateButtonOnClick() {
        if (orderAddValidator.validate()) {
            currentOrder.setOrderDate(txt_orderDate.getValue().atTime(LocalTime.now()));
            currentOrder.setDeliveryDate(txt_deliveryDate.getValue().atTime(LocalTime.now()));
            currentOrder.setReceiveDate(txt_receiveDate.getValue().atTime(LocalTime.now()));
            currentOrder.setDeliveryLocation(txt_deliveryLocation.getText());
//            currentOrder.setCustomerId(Long.parseLong(txt_customerId.getText()));
//            currentOrder.setEmployeeId(Long.parseLong(txt_employeeId.getText()));
            currentOrder.setCustomerId(currentCustomer.getId());
            currentOrder.setEmployeeId(currentEmployee.getId());

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
        txt_orderDate.setValue(LocalDate.now());
        txt_deliveryDate.setValue(LocalDate.now());
        txt_receiveDate.setValue(LocalDate.now());
        txt_customerPhone.clear();
        txt_customerName.clear();
//        txt_employeeId.clear();
        txt_deliveryLocation.clear();
        txt_note.clear();

        clearOrderDetail();
        clearValidateError();
    }

    private void clearValidateError() {
        lbl_error_orderDate.setVisible(false);
        lbl_error_deliveryDate.setVisible(false);
        lbl_error_shipmentDate.setVisible(false);
        lbl_error_customerId.setVisible(false);
//        lbl_error_employeeId.setVisible(false);
        lbl_error_deliveryLocation.setVisible(false);
        lbl_error_note.setVisible(false);
        lbl_error_orderDetailEmpty.setVisible(false);
    }

    @FXML
    void addOrderDetailsOnClick() {
        try {
            if (orderDetailScene == null && orderDetailStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order-detail/main-orderDetail-add.fxml"));
                orderDetailScene = new Scene(fxmlLoader.load());
                orderDetailStage = new Stage();
                orderDetailStage.setTitle("Order Details");
                orderDetailStage.initModality(Modality.APPLICATION_MODAL);
                orderDetailStage.setResizable(false);
                OrderDetailController controller = fxmlLoader.getController();
                controller.setStage(orderDetailStage);
                controller.setOrderDetails(orderDetails);
                controller.setVbox_orderDetail(vbox_orderDetail);
                controller.setLbl_totalAmount(lbl_totalAmount);

                orderDetailStage.setScene(orderDetailScene);
            }

            orderDetailStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void selectCustomerOnClick() {
        try {
            if (selectCustomerScene == null && selectCustomerStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order-add-selectCustomer.fxml"));
                selectCustomerScene = new Scene(fxmlLoader.load());
                selectCustomerStage = new Stage();
                OrderAddSelectCustomerController controller = fxmlLoader.getController();
                controller.setCustomerSelectionCallback(this);
                controller.setStage(selectCustomerStage);
                selectCustomerStage.setTitle("Select customer");
                selectCustomerStage.initModality(Modality.APPLICATION_MODAL);
                selectCustomerStage.setResizable(false);

                selectCustomerStage.setScene(selectCustomerScene);
            }

            selectCustomerStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void clearOrderDetail() {
        vbox_orderDetail.getChildren().clear();
        orderDetails.clear();
        lbl_totalAmount.setText("");
    }

    @FXML
    void test() {
        System.out.println(orderDetails);
    }
}
