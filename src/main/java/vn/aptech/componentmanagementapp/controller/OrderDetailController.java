package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.ProductInfoView;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDetailController implements Initializable {

    @FXML
    private HBox hbox_addButtonGroup;
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
    private Label lbl_error_productId;

    @FXML
    private Label lbl_error_quantity;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private MFXTextField txt_discount;

    @FXML
    private MFXTextField txt_productId;

    @FXML
    private MFXTextField txt_quantity;
    // Service
    ProductService productService = new ProductService();
    // List order details
    private List<OrderDetail> orderDetails;
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    //  Validator
    Validator productIdValidator = new Validator();
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
//        productIdValidator.createCheck()
//                .dependsOn("productId", txt_productId.textProperty())
//                .withMethod(context -> {
//                    String productId = context.get("productId");
//                    if (!productId.matches("\\d+")) {
//                        context.error("ProductId only contain number");
//                    }
//                })
//                .decoratingWith(this::labelDecorator)
//                .decorates(lbl_error_productId);

        orderDetailsValidator.createCheck()
                .dependsOn("productId", txt_productId.textProperty())
                .withMethod(context -> {
                    String productId = context.get("productId");
                    if (productId.isEmpty()) {
                        context.error("ProductId can't be empty");
                    } else if (!productId.matches("\\d+")) {
                        context.error("ProductId only contain number");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_productId);

        orderDetailsValidator.createCheck()
                .dependsOn("quantity", txt_quantity.textProperty())
                .withMethod(context -> {
                    String quantity = context.get("quantity");
                    if (quantity.isEmpty()) {
                        context.error("Quantity can't be empty");
                    } else if (!quantity.matches("\\d+")) {
                        context.error("Quantity only contain number");
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
        txt_productId.textProperty().addListener((observable, oldValue, newValue) -> {
            updateProductDetails();
        });

        txt_quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            updateProductDetails();
        });

        txt_discount.textProperty().addListener((observable, oldValue, newValue) -> {
            updateProductDetails();
        });
    }

    // Sự kiện chung để cập nhật chi tiết sản phẩm
    private void updateProductDetails() {
        if (orderDetailsValidator.validate()) {
            Product product = productService.getProductById(Long.parseLong(txt_productId.getText()));

            if (product != null) {
                double price = product.getPrice();
                int quantity = Integer.parseInt(txt_quantity.getText());
                double discount = Double.parseDouble(txt_discount.getText()) / 100 * price;
                double totalDiscount = Double.parseDouble(txt_discount.getText()) / 100 * price * quantity;
                double totalAmount = (price - discount) * quantity;

                lbl_productName.setText(product.getName());
                lbl_productPrice.setText(decimalFormat.format(price));
                lbl_productPrice_discount.setText("- " + decimalFormat.format(totalDiscount));
                lbl_productTotalAmount.setText(decimalFormat.format(totalAmount));
            } else {
                lbl_productName.setText("This id doesn't belong to any product");
                lbl_productName.setTextFill(Paint.valueOf("#e57c23"));
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
            OrderDetail orderDetail = new OrderDetail();

            Product product = productService.getProductById(Long.parseLong(txt_productId.getText()));

            double price = product.getPrice();
            int quantity = Integer.parseInt(txt_quantity.getText());
            int discount = Integer.parseInt(txt_discount.getText());
            double discountPrice = Double.parseDouble(txt_discount.getText()) / 100 * price;

            double totalAmount = (price - discountPrice) * quantity;

            orderDetail.setName(product.getName());
            orderDetail.setPrice(price);
            orderDetail.setDiscount(discount);
            orderDetail.setTotalAmount(totalAmount);
            orderDetail.setProductId(product.getId());

            orderDetails.add(orderDetail);

            double totalOrderAmount = 0;

            for (OrderDetail od: orderDetails) {
                totalOrderAmount = totalOrderAmount + od.getTotalAmount();
            }

            lbl_totalAmount.setText(decimalFormat.format(totalOrderAmount));

            // Show success message
//            lbl_successMessage.setText("Add new order detail successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 4 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();

            System.out.println(product.getName());

            ProductInfoView productInfoView = new ProductInfoView();
            productInfoView.getLblProductName().setText(product.getName());
            productInfoView.getLblProductPrice().setText(decimalFormat.format(price));
            productInfoView.getLblProductDiscount().setText(String.valueOf(discount));
            productInfoView.getLblProductQuantity().setText(String.valueOf(quantity));
            productInfoView.getLblProductTotalAmount().setText(decimalFormat.format(totalAmount));
            productInfoView.setVbox_orderDetail(vbox_orderDetail);
            productInfoView.setOrderDetail(orderDetail);
            productInfoView.setOrderDetails(orderDetails);

            vbox_orderDetail.getChildren().add(productInfoView);
        }
    }

}
