package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
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
import vn.aptech.componentmanagementapp.service.CustomerService;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.service.OrderService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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

    //  Validator
    private final Validator orderAddValidator = new Validator();
    private final Validator customerIdValidator = new Validator();
    private final Validator employeeIdValidator = new Validator();

    // Service
    private final OrderService orderService = new OrderService();
    private final CustomerService customerService = new CustomerService();
    private final EmployeeService employeeService = new EmployeeService();

    // Cache order details
    private Scene orderDetailScene;
    private Stage orderDetailStage;

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

        initEvent();
        initValidator();
    }

    private void initValidator() {
        customerIdValidator.createCheck()
                .dependsOn("customerId", txt_customerId.textProperty())
                .withMethod(context -> {
                    String customerId = context.get("customerId");
                    if (!customerId.matches("\\d+")) {
                        context.error("CustomerId only contain number");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerId);

        employeeIdValidator.createCheck()
                .dependsOn("employeeId", txt_employeeId.textProperty())
                .withMethod(context -> {
                    String employeeId = context.get("employeeId");
                    if (!employeeId.matches("\\d+")) {
                        context.error("EmployeeId only contain number");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_employeeId);

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
                .dependsOn("shipmentDate", txt_shipmentDate.valueProperty())
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
                .dependsOn("customerId", txt_customerId.textProperty())
                .withMethod(context -> {
                   String customerId = context.get("customerId");
                   if (customerId.matches("\\d+")) {
                       Customer customer = customerService.getCustomerById(Long.parseLong(customerId));
                       if (customer == null)
                           context.error("Customer id don't belong any customer");
                   }
                });

        orderAddValidator.createCheck()
                .dependsOn("employeeId", txt_employeeId.textProperty())
                .withMethod(context -> {
                    String employeeId = context.get("employeeId");
                    if (employeeId.matches("\\d+")) {
                        Employee employee = employeeService.getEmployeeById(Long.parseLong(employeeId));
                        if (employee == null)
                            context.error("Customer id don't belong any customer");
                    }
                });
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
        if (orderAddValidator.validate()) {
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

    private void initEvent() {
        txt_customerId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (customerIdValidator.validate()) {
                lbl_error_customerId.setVisible(true);
                Customer customer = customerService.getCustomerById(Long.parseLong(newValue));
                if (customer != null) {
                    lbl_error_customerId.setText("Customer name: " + customer.getName());
                    lbl_error_customerId.setTextFill(Paint.valueOf("#70da6a"));
                } else {
                    lbl_error_customerId.setText("This id don't belong to any customer");
                    lbl_error_customerId.setTextFill(Paint.valueOf("#e57c23"));
                }
            } else if (newValue.isEmpty())
                lbl_error_customerId.setVisible(false);
        });

        txt_employeeId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (employeeIdValidator.validate()) {
                lbl_error_employeeId.setVisible(true);
                Employee employee = employeeService.getEmployeeById(Long.parseLong(newValue));
                if (employee != null) {
                    lbl_error_employeeId.setText("Employee name: " + employee.getName());
                    lbl_error_employeeId.setTextFill(Paint.valueOf("#70da6a"));
                } else {
                    lbl_error_employeeId.setText("This id don't belong to any employee");
                    lbl_error_employeeId.setTextFill(Paint.valueOf("#e57c23"));
                }
            } else if (newValue.isEmpty())
                lbl_error_employeeId.setVisible(false);
        });
    }

    @FXML
    void addOrderDetailsOnClick() {
        try {
            if (orderDetailScene == null && orderDetailStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order-detail/main-orderDetail-add.fxml"));
                orderDetailScene = new Scene(fxmlLoader.load());
                orderDetailStage = new Stage();
                orderDetailStage.setTitle("Order Details");
                OrderDetailController controller = fxmlLoader.getController();
                controller.setStage(orderDetailStage);

                orderDetailStage.setScene(orderDetailScene);
                orderDetailStage.setResizable(false);
            }

            orderDetailStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
