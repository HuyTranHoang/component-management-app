package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class OrderDetailController implements Initializable {

    @FXML
    private HBox hbox_addButtonGroup;

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
    private MFXTextField txt_discount;

    @FXML
    private MFXTextField txt_productId;

    @FXML
    private MFXTextField txt_quantity;
    // Service
    ProductService productService = new ProductService();

    //  Validator
    Validator productIdValidator = new Validator();
    Validator orderDetailsValidator = new Validator();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
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
        DecimalFormat decimalFormat = new DecimalFormat("₫#,##0");
        if (orderDetailsValidator.validate()) {
            Product product = productService.getProductById(Long.parseLong(txt_productId.getText()));

            if (product != null) {
                double price = product.getPrice();
                double discount = 0;
                int quantity = 1;

                lbl_productName.setText(product.getName());
                lbl_productName.setTextFill(Paint.valueOf("#4A55A2"));

                lbl_productPrice.setText(decimalFormat.format(price));

                if (!txt_discount.getText().isEmpty()) {
                    discount = Double.parseDouble(txt_discount.getText()) / 100 * price;
                }
                lbl_productPrice_discount.setText(decimalFormat.format(discount));

                if (!txt_quantity.getText().isEmpty()) {
                    quantity = Integer.parseInt(txt_quantity.getText());
                }

                double totalAmount = (price - discount) * quantity;
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
        }
    }


}
