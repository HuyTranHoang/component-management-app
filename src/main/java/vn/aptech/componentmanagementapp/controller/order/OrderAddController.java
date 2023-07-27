package vn.aptech.componentmanagementapp.controller.order;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
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
import vn.aptech.componentmanagementapp.controller.orderdetail.OrderDetailSelectProductController;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.OrderDetailService;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.ProductInfoView;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderAddController implements Initializable, OrderAddSelectCustomerController.CustomerSelectionCallback,
        OrderDetailSelectProductController.ProductSelectionCallback{

    @Override
    public void onCustomerSelected(Customer customer) {
        currentCustomer = customer;
        txt_customerPhone.setText(customer.getPhone());
        txt_customerName.setText(customer.getName());
    }

    @Override
    public void onProductSelected(Product product) {
        if (product != null) {
            currentProduct = product;
            if (orderDetailsValidator.validate()) {
                double price = product.getPrice();
                int quantity = Integer.parseInt(txt_quantity.getText());
                double discount = Double.parseDouble(txt_discount.getText()) / 100 * price;
                double totalDiscount = Double.parseDouble(txt_discount.getText()) / 100 * price * quantity;
                double totalAmount = (price - discount) * quantity;

                lbl_productName.setText(product.getName());
                lbl_productPrice.setText(decimalFormat.format(price));
                lbl_productPrice_discount.setText("- " + decimalFormat.format(totalDiscount));
                lbl_productTotalAmount.setText(decimalFormat.format(totalAmount));
            }
        }
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

    //
    private Product currentProduct;

    private OrderAddSelectCustomerController orderAddSelectCustomerController;

    //


    @FXML
    private AnchorPane anchor_order;

    @FXML
    private AnchorPane anchor_orderDetail;

    @FXML
    private Label lbl_error_product;
    @FXML
    private Label lbl_error_quantity;
    @FXML
    private Label lbl_error_discount;

    @FXML
    private Label lbl_productName;

    @FXML
    private Label lbl_productPrice;

    @FXML
    private Label lbl_productPrice_discount;

    @FXML
    private Label lbl_productTotalAmount;

    @FXML
    private HBox hbox_addButtonGroup;

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private HBox hbox_storeButtonGroup;

    @FXML
    private AnchorPane anchor_inputOrderDetails;

    @FXML
    private AnchorPane anchor_showOrder;

    @FXML
    private Label lbl_showCustomerName;

    @FXML
    private Label lbl_showCustomerPhone;

    @FXML
    private Label lbl_showDeliveryDate;

    @FXML
    private Label lbl_showDeliveryLocation;

    @FXML
    private Label lbl_showEmployeeName;

    @FXML
    private Label lbl_showNote;

    @FXML
    private Label lbl_showOrderDate;

    @FXML
    private Label lbl_showReceiveDate;

    // Current index order details for update
    private int currentIndex;
    private ProductInfoView currentProductInfoView;

    //


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
    private Label lbl_error_customerEmpty;

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

    @FXML
    private MFXTextField txt_quantity;

    @FXML
    private MFXTextField txt_discount;

    @FXML
    private MFXButton btn_clearList;


    //  Validator
    private final Validator orderAddValidator = new Validator();
    private final Validator orderDetailsValidator = new Validator();
    private final Validator totalOrderDetailsValidator = new Validator();

    //  Formatter
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

    // Service
    private final OrderService orderService = new OrderService();
    private final OrderDetailService orderDetailService = new OrderDetailService();
    private final ProductService productService = new ProductService();

    // Controller
    private OrderDetailSelectProductController orderDetailSelectProductController;

    // Cache order details
    private Scene orderDetailScene;
    private Stage orderDetailStage;
    private Scene selectProductScene;
    private Stage selectProductStage;

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
        txt_deliveryDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_deliveryDate.getLocale()));
        txt_receiveDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_receiveDate.getLocale()));

        initValidator();
//        initEnterKeyPressing();
        initEvent();

        Platform.runLater(() -> txt_employeeName.setText(currentEmployee.getName()));
    }

    public void setTextEmployeeName(Employee employee) {
        txt_employeeName.setText(employee.getName());
    }

    private void initValidator() {
        orderAddValidator.createCheck()
                    .withMethod(context -> {
                        if (currentCustomer == null)
                            context.error("Please select customer to store");
                    })
                    .decoratingWith(this::labelDecorator)
                    .decorates(lbl_error_customerEmpty);

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
                    } else if (deliveryDate.isBefore(orderDate)) {
                        context.error("Delivery date can't be before order date");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_deliveryDate);

        orderAddValidator.createCheck()
                .dependsOn("receiveDate", txt_receiveDate.valueProperty())
                .dependsOn("deliveryDate", txt_deliveryDate.valueProperty())
                .dependsOn("orderDate", txt_orderDate.valueProperty())
                .withMethod(context -> {
                    LocalDate receiveDate = context.get("receiveDate");
                    LocalDate deliveryDate = context.get("deliveryDate");
                    LocalDate orderDate = context.get("orderDate");
                    if (receiveDate == null) {
                        context.error("Receive date can't be empty");
                    } else if (receiveDate.isBefore(deliveryDate)) {
                        context.error("Receive date can't be before delivery date");
                    } else if (receiveDate.isBefore(orderDate)) {
                        context.error("Receive date can't be before order date");
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

        //

        orderDetailsValidator.createCheck()
                .withMethod(context -> {
                    if (currentProduct == null) {
                        context.error("Please select product");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_product);


        orderDetailsValidator.createCheck()
                .dependsOn("quantity", txt_quantity.textProperty())
                .withMethod(context -> {
                    String quantity = context.get("quantity");
                    if (currentProduct != null) {
                        if (quantity.isEmpty()) {
                            context.error("Quantity can't be empty");
                        } else if (!quantity.matches("\\d+")) {
                            context.error("Quantity only contain number");
                        } else if (Integer.parseInt(quantity) > currentProduct.getStockQuantity())
                            context.error("Exceed the remaining quantity");
                    }

                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_quantity);

        orderDetailsValidator.createCheck()
                .dependsOn("discount", txt_discount.textProperty())
                .withMethod(context -> {
                    String discount = context.get("discount");
                    if (discount.isEmpty()) {
                        context.error("Discount can't be empty");
                    } else if (!discount.matches("\\d+")) {
                        context.error("Discount only contain number");
                    } else if (Integer.parseInt(discount) > 15)
                        context.error("Can't discount more than 15%");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_discount);

        totalOrderDetailsValidator.createCheck()
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


    @FXML
    void listOrderButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/order.fxml"));
                orderView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }


    @FXML
    void saveButtonOnClick(){
        if(orderAddValidator.validate()){
            double totalAmount = 0;
            for (OrderDetail orderDetail: orderDetails) {
                totalAmount += orderDetail.getTotalAmount();
            }
            currentOrder.setTotalAmount(totalAmount);

            long orderId = orderService.addOrderReturnId(currentOrder);
            orderDetails.forEach(orderDetail -> {
                orderDetail.setOrderId(orderId);
                orderDetailService.addOrderDetail(orderDetail);
                productService.updateProductExportQuantity(orderDetail.getProductId(), orderDetail.getQuantity());
            });

            // Pass the newly added product to the callback
            if (orderAddCallback != null) {
                orderAddCallback.onOrderAdded(currentOrder);
            }

            // Show success message
            lbl_successMessage.setText("Add new order successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 4 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();

            clearInputOrder();

            anchor_inputOrderDetails.setVisible(true);
            anchor_showOrder.setVisible(false);

            hbox_addButtonGroup.setVisible(true);
            hbox_storeButtonGroup.setVisible(false);
        }
    }

    @FXML
    void clearInputOrder() { // Được gọi trước khi vào add view ở Order Add Controller
        anchor_order.setVisible(true);
        anchor_orderDetail.setVisible(false);

        lbl_totalAmount.setText("...");

        txt_orderDate.setValue(LocalDate.now());
        txt_deliveryDate.setValue(LocalDate.now());
        txt_receiveDate.setValue(LocalDate.now());
        txt_customerPhone.clear();
        txt_customerName.clear();
//        txt_employeeId.clear();
        currentCustomer = null;

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
        lbl_error_customerEmpty.setVisible(false);
        lbl_error_deliveryLocation.setVisible(false);
        lbl_error_note.setVisible(false);
        lbl_error_orderDetailEmpty.setVisible(false);
    }

    @FXML
    void clearInputOrderDetail() {
        currentProduct = null;
        lbl_productName.setText("");
        lbl_productPrice.setText("");
        lbl_productPrice_discount.setText("");
        lbl_productTotalAmount.setText("");


        txt_quantity.setText("1");
        txt_discount.setText("0");

        lbl_error_product.setVisible(false);
    }

    @FXML
    void selectCustomerOnClick() {
        try {
            if (selectCustomerScene == null && selectCustomerStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/order-add-selectCustomer.fxml"));
                selectCustomerScene = new Scene(fxmlLoader.load());
                selectCustomerStage = new Stage();
                orderAddSelectCustomerController = fxmlLoader.getController();
                orderAddSelectCustomerController.setCustomerSelectionCallback(this);
                orderAddSelectCustomerController.setStage(selectCustomerStage);
                selectCustomerStage.setTitle("Select customer");

                Image image = null;

                URL resourceURL = ComponentManagementApplication.class.getResource("images/customer.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }

                selectCustomerStage.getIcons().add(image);

                selectCustomerStage.initModality(Modality.APPLICATION_MODAL);
                selectCustomerStage.setResizable(false);

                selectCustomerStage.setScene(selectCustomerScene);
            }
            orderAddSelectCustomerController.reloadCustomer();
            selectCustomerStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void selectProductOnClick() {
        try {
            if (selectProductScene == null && selectProductStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class
                        .getResource("fxml/order-detail/orderDetail-add-selectProduct.fxml"));
                selectProductScene = new Scene(fxmlLoader.load());
                selectProductStage = new Stage();
                orderDetailSelectProductController = fxmlLoader.getController();
                orderDetailSelectProductController.setProductSelectionCallback(this);
                orderDetailSelectProductController.setStage(selectProductStage);
                selectProductStage.setTitle("Select customer");

                Image image = null;

                URL resourceURL = ComponentManagementApplication.class.getResource("images/product.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }

                selectProductStage.getIcons().add(image);

                selectProductStage.initModality(Modality.APPLICATION_MODAL);

                selectProductStage.setResizable(false);

                selectProductStage.setScene(selectProductScene);
            }

            orderDetailSelectProductController.reloadProduct();
            selectProductStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void nextButtonOnClickOrder() {
            if(orderAddValidator.validate()) {
                currentOrder = new Order();
                currentOrder.setOrderDate(txt_orderDate.getValue().atTime(LocalTime.now()));
                currentOrder.setDeliveryDate(txt_deliveryDate.getValue().atTime(LocalTime.now()));
                currentOrder.setReceiveDate(txt_receiveDate.getValue().atTime(LocalTime.now()));
                currentOrder.setDeliveryLocation(txt_deliveryLocation.getText());
                currentOrder.setCustomerId(currentCustomer.getId());
                currentOrder.setEmployeeId(currentEmployee.getId());
                currentOrder.setNote(txt_note.getText());

                anchor_order.setVisible(false);
                anchor_orderDetail.setVisible(true);
            }
    }

    @FXML
    void nextButtonOnClickOrderDetail() {
        if (totalOrderDetailsValidator.validate())
            storeMode();
    }

    @FXML
    void addButtonOnClick() {
        if (orderDetailsValidator.validate()) {
            double price = currentProduct.getPrice();
            int quantity = Integer.parseInt(txt_quantity.getText());
            int discount = Integer.parseInt(txt_discount.getText());
            double discountPrice = (discount / 100.0) * price;
            double totalAmount = (price - discountPrice) * quantity;

            OrderDetail existingOrderDetail = findExistingOrderDetail(currentProduct.getName());
            if (existingOrderDetail != null) {
                int stockQuantity = currentProduct.getStockQuantity();
                int newQuantity = existingOrderDetail.getQuantity() + quantity;

                if (newQuantity > stockQuantity) {
                    // Hiển thị cảnh báo khi newQuantity vượt quá stockQuantity
                    showAlert("Error", "Not enough stock available!", Alert.AlertType.ERROR);
                    return;
                }

                double newTotalAmount = (price - (existingOrderDetail.getDiscount() / 100.0) * price) * newQuantity;

                existingOrderDetail.setQuantity(newQuantity);
                existingOrderDetail.setTotalAmount(newTotalAmount);

                updateProductInfoView(existingOrderDetail);
            } else {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setName(currentProduct.getName());
                orderDetail.setPrice(price);
                orderDetail.setQuantity(quantity);
                orderDetail.setDiscount(discount);
                orderDetail.setTotalAmount(totalAmount);
                orderDetail.setProductId(currentProduct.getId());

                orderDetails.add(orderDetail);
                addProductInfoView(orderDetail);
            }

            updateTotalOrderAmount();
            clearInputOrderDetail();

        }
    }

    // Tìm kiếm OrderDetail đã tồn tại trong danh sách orderDetails bằng tên sản phẩm
    private OrderDetail findExistingOrderDetail(String productName) {
        for (OrderDetail od : orderDetails) {
            if (od.getName().equals(productName)) {
                return od;
            }
        }
        return null;
    }

    // Hiển thị cảnh báo
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateProductInfoView(OrderDetail existingOrderDetail) {
        for (Node node : vbox_orderDetail.getChildren()) {
            if (node instanceof ProductInfoView) {
                ProductInfoView productView = (ProductInfoView) node;
                if (productView.getLblProductName().getText().equals(existingOrderDetail.getName())) {
                    productView.getLblProductQuantity().setText(String.valueOf(existingOrderDetail.getQuantity()));
                    productView.getLblProductTotalAmount().setText(decimalFormat.format(existingOrderDetail.getTotalAmount()));
                    break;
                }
            }
        }
    }

    // Thêm productInfoView mới vào vbox_orderDetail
    private void addProductInfoView(OrderDetail orderDetail) {
        ProductInfoView productInfoView = new ProductInfoView();
        productInfoView.getLblProductName().setText(orderDetail.getName());
        productInfoView.getLblProductPrice().setText(decimalFormat.format(orderDetail.getPrice()));
        productInfoView.getLblProductDiscount().setText(String.valueOf(orderDetail.getDiscount()));
        productInfoView.getLblProductQuantity().setText(String.valueOf(orderDetail.getQuantity()));
        productInfoView.getLblProductTotalAmount().setText(decimalFormat.format(orderDetail.getTotalAmount()));
        productInfoView.setVbox_orderDetail(vbox_orderDetail);
        productInfoView.setOrderDetail(orderDetail);
        productInfoView.setOrderDetails(orderDetails);
        productInfoView.setEditButtonAction(event -> {
            updateMode();
            currentIndex = vbox_orderDetail.getChildren().indexOf(productInfoView);
            currentProductInfoView = productInfoView;
            currentProduct = productService.getProductByName(productInfoView.getLblProductName().getText());
            if (orderDetailsValidator.validate()) {
                double ePrice = currentProduct.getPrice();
                int eQuantity = Integer.parseInt(productInfoView.getLblProductQuantity().getText());
                double eDiscount = Double.parseDouble(productInfoView.getLblProductDiscount().getText()) / 100 * ePrice;
                double eTotalDiscount = Double.parseDouble(productInfoView.getLblProductDiscount().getText()) / 100 * ePrice * eQuantity;
                double eTotalAmount = (ePrice - eDiscount) * eQuantity;

                lbl_productName.setText(currentProduct.getName());
                lbl_productPrice.setText(decimalFormat.format(ePrice));
                lbl_productPrice_discount.setText("- " + decimalFormat.format(eTotalDiscount));
                lbl_productTotalAmount.setText(decimalFormat.format(eTotalAmount));
                txt_quantity.setText(String.valueOf(eQuantity));
                txt_discount.setText(String.valueOf(productInfoView.getLblProductDiscount().getText()));
            }
        });

        productInfoView.setRemoveButtonAction(event -> {
            vbox_orderDetail.getChildren().remove(productInfoView);
            orderDetails.remove(orderDetail);
            updateTotalOrderAmount();
        });

        vbox_orderDetail.getChildren().add(productInfoView);
    }

    // Cập nhật tổng số tiền của đơn hàng
    private void updateTotalOrderAmount() {
        double totalOrderAmount = 0;
        for (OrderDetail od : orderDetails) {
            totalOrderAmount += od.getTotalAmount();
        }
        lbl_totalAmount.setText(decimalFormat.format(totalOrderAmount));
    }

    public void addMode() {
        hbox_addButtonGroup.setVisible(true);
        hbox_updateButtonGroup.setVisible(false);
        hbox_storeButtonGroup.setVisible(false);

        btn_clearList.setVisible(true);
    }

    public void updateMode() {
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(true);
        hbox_storeButtonGroup.setVisible(false);
    }

    public void storeMode() {
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(false);
        hbox_storeButtonGroup.setVisible(true);

        btn_clearList.setVisible(false);

        anchor_inputOrderDetails.setVisible(false);
        anchor_showOrder.setVisible(true);

        for (Node node : vbox_orderDetail.getChildren()) {
            if (node instanceof ProductInfoView) {
                ProductInfoView productView = (ProductInfoView) node;
                productView.getBtnEdit().setVisible(false);
                productView.getBtnRemove().setVisible(false);
            }
        }

        setInformation();
    }

    @FXML
    void updateButtonOnClick() {
        if (orderDetailsValidator.validate()) {
            OrderDetail orderDetail = orderDetails.get(currentIndex);
            double price = currentProduct.getPrice();
            int quantity = Integer.parseInt(txt_quantity.getText());
            int discount = Integer.parseInt(txt_discount.getText());
            double discountPrice = Double.parseDouble(txt_discount.getText()) / 100 * price;
            double totalAmount = (price - discountPrice) * quantity;

            orderDetail.setName(currentProduct.getName());
            orderDetail.setPrice(price);
            orderDetail.setQuantity(quantity);
            orderDetail.setDiscount(discount);
            orderDetail.setTotalAmount(totalAmount);
            orderDetail.setProductId(currentProduct.getId());

            ProductInfoView productInfoView = currentProductInfoView;
            productInfoView.getLblProductName().setText(currentProduct.getName());
            productInfoView.getLblProductPrice().setText(decimalFormat.format(price));
            productInfoView.getLblProductDiscount().setText(String.valueOf(discount));
            productInfoView.getLblProductQuantity().setText(String.valueOf(quantity));
            productInfoView.getLblProductTotalAmount().setText(decimalFormat.format(totalAmount));
            productInfoView.setVbox_orderDetail(vbox_orderDetail);
            productInfoView.setOrderDetail(orderDetail);
            productInfoView.setOrderDetails(orderDetails);
            clearInputOrderDetail();
            addMode();
        }
    }

    @FXML
    void backButtonOnClick() {
        if (anchor_inputOrderDetails.visibleProperty().get()) {
            clearInputOrderDetail();
            addMode();
        } else {
            anchor_inputOrderDetails.setVisible(true);
            anchor_showOrder.setVisible(false);
            addMode();

            for (Node node : vbox_orderDetail.getChildren()) {
                if (node instanceof ProductInfoView) {
                    ProductInfoView productView = (ProductInfoView) node;
                    productView.getBtnEdit().setVisible(true);
                    productView.getBtnRemove().setVisible(true);
                }
            }
        }

    }

    private void initEvent() {
        txt_quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            updateProductDetails();
        });

        txt_discount.textProperty().addListener((observable, oldValue, newValue) -> {
            updateProductDetails();
        });
    }

    private void updateProductDetails() {
        if (orderDetailsValidator.validate()) {
            if (currentProduct != null) {
                double price = currentProduct.getPrice();
                int quantity = Integer.parseInt(txt_quantity.getText());
                double discount = Double.parseDouble(txt_discount.getText()) / 100 * price;
                double totalDiscount = Double.parseDouble(txt_discount.getText()) / 100 * price * quantity;
                double totalAmount = (price - discount) * quantity;

                lbl_productName.setText(currentProduct.getName());
                lbl_productPrice.setText(decimalFormat.format(price));
                lbl_productPrice_discount.setText("- " + decimalFormat.format(totalDiscount));
                lbl_productTotalAmount.setText(decimalFormat.format(totalAmount));
            }
        } else {
            lbl_productName.setText("...");
            lbl_productPrice.setText("...");
            lbl_productPrice_discount.setText("...");
            lbl_productTotalAmount.setText("...");
        }
    }

    void setInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

        Customer currentCustomer = currentOrder.getCustomer();
        Employee currentEmployee = currentOrder.getEmployee();

        lbl_showOrderDate.setText(formatter.format(currentOrder.getOrderDate()));
        lbl_showDeliveryDate.setText(formatter.format(currentOrder.getDeliveryDate()));
        lbl_showReceiveDate.setText(formatter.format(currentOrder.getReceiveDate()));

        lbl_showCustomerName.setText(currentCustomer.getName());
        lbl_showCustomerPhone.setText(currentCustomer.getPhone());

        lbl_showEmployeeName.setText(currentEmployee.getName());

        lbl_showDeliveryLocation.setText(currentOrder.getDeliveryLocation());
        lbl_showNote.setText(currentOrder.getNote());

        double totalAmount = 0;

        for (OrderDetail orderDetail: orderDetails) {
            totalAmount += orderDetail.getTotalAmount();
        }
        lbl_totalAmount.setText(decimalFormat.format(totalAmount));
    }


    @FXML
    void clearOrderDetail() {
        vbox_orderDetail.getChildren().clear();
        orderDetails.clear();
    }

    @FXML
    void test() {
        System.out.println(orderDetails);
    }
}
