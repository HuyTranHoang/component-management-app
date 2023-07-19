package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.ProductInfoView;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDetailController implements Initializable, OrderDetailSelectProductController.ProductSelectionCallback {

    @FXML
    private HBox hbox_addButtonGroup;
    @FXML
    private HBox hbox_updateButtonGroup;
    private VBox vbox_orderDetail;

    public void setVbox_orderDetail(VBox vbox_orderDetail) {
        this.vbox_orderDetail = vbox_orderDetail;
    }

    @FXML
    private Label lbl_productName;

    @FXML
    private Label lbl_productPrice;

    @FXML
    private Label lbl_productPrice_discount;

    @FXML
    private Label lbl_productTotalAmount;

    @FXML
    private Label lbl_error_discount;

    @FXML
    private Label lbl_error_quantity;

    @FXML
    private Label lbl_error_product;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private MFXTextField txt_discount;

    @FXML
    private MFXTextField txt_quantity;
    // Service
    ProductService productService = new ProductService();
    // List order details
    private List<OrderDetail> orderDetails;
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    // Current index order details for update
    private int currentIndex;
    private ProductInfoView currentProductInfoView;

    // Cache view
    private Scene selectProductScene;
    private Stage selectProductStage;

    private Product currentProduct;

    //  Validator
    Validator orderDetailsValidator = new Validator();

    //  Formatter
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

    private Stage stage;
    private Label lbl_totalAmount;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLbl_totalAmount(Label lbl_totalAmount) {
        this.lbl_totalAmount = lbl_totalAmount;
    }

    @FXML
    void backButtonOnClick() {
        stage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initValidator();
        initEvent();

    }

    private void initValidator() {
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

//            showSuccessMessage("Add new order details successfully!");

            stage.close();
            clearInput();
        }
    }

    // Hiển thị cảnh báo
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

    // Cập nhật thông tin của productInfoView khi OrderDetail đã tồn tại trong danh sách
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
            stage.show();
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

    // Hiển thị thông báo thành công
    private void showSuccessMessage(String message) {
        lbl_successMessage.setVisible(true);
        lbl_successMessage.setText(message);
        new FadeIn(lbl_successMessage).play();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            new FadeOut(lbl_successMessage).play();
        }));
        timeline.play();
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

//            showSuccessMessage("Update order details successfully!");
            stage.close();
        }

    }

    @FXML
    void selectProductOnClick() {
        try {
            if (selectProductScene == null && selectProductStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class
                        .getResource("fxml/order-detail/main-orderDetail-add-selectProduct.fxml"));
                selectProductScene = new Scene(fxmlLoader.load());
                selectProductStage = new Stage();
                OrderDetailSelectProductController controller = fxmlLoader.getController();
                controller.setProductSelectionCallback(this);
                controller.setStage(selectProductStage);
                selectProductStage.setTitle("Select customer");
                selectProductStage.initModality(Modality.APPLICATION_MODAL);
                selectProductStage.setResizable(false);

                selectProductStage.setScene(selectProductScene);
            }

            selectProductStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearInput() {
        currentProduct = null;
        lbl_productName.setText("");
        lbl_productPrice.setText("");
        lbl_productPrice_discount.setText("");
        lbl_productTotalAmount.setText("");


        txt_quantity.setText("1");
        txt_discount.setText("0");

        lbl_error_product.setVisible(false);
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

    public void addMode() {
        hbox_addButtonGroup.setVisible(true);
        hbox_updateButtonGroup.setVisible(false);
    }

    public void updateMode() {
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(true);
    }
}
