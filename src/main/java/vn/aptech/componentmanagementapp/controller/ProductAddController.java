package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.CategoryService;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.service.SupplierService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ProductAddController implements Initializable {

    // Call back product
    public interface ProductAddCallback {
        void onProductAdded(Product product);
    }
    private ProductAddCallback productAddCallback;
    public void setProductAddCallback(ProductAddCallback productAddCallback) {
        this.productAddCallback = productAddCallback;
    }

    // Cached views
    private AnchorPane productView;

    private AnchorPane anchor_main_rightPanel; // Truyền từ Product controller vào

    //  List
    private ObservableList<Category> categories;
    private ObservableList<Supplier> suppliers;

    //  Validator
    Validator productValidator = new Validator();

    @FXML
    private Label lbl_error_name;
    @FXML
    private Label lbl_error_price;
    @FXML
    private Label lbl_error_productCode;
    @FXML
    private Label lbl_error_stockQuantity;
    @FXML
    private Label lbl_successMessage;
    @FXML
    private Label lbl_error_monthOfWarranty;
    @FXML
    private Label lbl_error_note;

    // Add Product Panel
    @FXML
    private MFXComboBox<Category> cbb_category;
    @FXML
    private MFXComboBox<Supplier> cbb_supplier;
    @FXML
    private MFXTextField txt_monthOfWarranty;
    @FXML
    private MFXTextField txt_name;
    @FXML
    private MFXTextField txt_note;
    @FXML
    private MFXTextField txt_price;
    @FXML
    private MFXTextField txt_productCode;
    @FXML
    private MFXTextField txt_stockQuantity;

    //  Service
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final SupplierService supplierService = new SupplierService();

    public void setProductView(AnchorPane productView) {
        this.productView = productView;
    }

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CompletableFuture.supplyAsync(categoryService::getAllCategory)
                .thenAcceptAsync(categoryList -> {
                    categories = FXCollections.observableArrayList(categoryList);
                })
                .thenComposeAsync(__ -> CompletableFuture.supplyAsync(supplierService::getAllSupplier))
                .thenAcceptAsync(supplierList -> {
                    suppliers = FXCollections.observableArrayList(supplierList);
                    Platform.runLater(() -> {
                        cbb_category.setItems(categories);
                        cbb_category.getSelectionModel().selectFirst();
                        cbb_supplier.setItems(suppliers);
                        cbb_supplier.getSelectionModel().selectFirst();

                        initValidator();
                    });
                })
                .join();
    }


    private void initValidator() {
        productValidator.createCheck()
                .dependsOn("productCode", txt_productCode.textProperty())
                .withMethod(context -> {
                    String productCode = context.get("productCode");
                    if (productCode.isEmpty())
                        context.error("Product code can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_productCode);

        productValidator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String name = context.get("name");
                    if (name.isEmpty())
                        context.error("Name can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_name);

        productValidator.createCheck()
                .dependsOn("price", txt_price.textProperty())
                .withMethod(context -> {
                    String price = context.get("price");
                    if (price.isEmpty())
                        context.error("Price can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_price);

        productValidator.createCheck()
                .dependsOn("stockQuantity", txt_stockQuantity.textProperty())
                .withMethod(context -> {
                    String stockQuantity = context.get("stockQuantity");
                    if (stockQuantity.isEmpty())
                        context.error("Stock quantity can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_stockQuantity);

        productValidator.createCheck()
                .dependsOn("monthOfWarranty", txt_monthOfWarranty.textProperty())
                .withMethod(context -> {
                    String monthOfWarranty = context.get("monthOfWarranty");
                    if (monthOfWarranty.isEmpty())
                        context.error("Month of warranty can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_monthOfWarranty);

        productValidator.createCheck()
                .dependsOn("note", txt_note.textProperty())
                .withMethod(context -> {
                    String note = context.get("note");
                    if (note.isEmpty())
                        context.error("Note can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_note);
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
    void listProductButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product.fxml"));
                productView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(productView);
    }

    @FXML
    void storeButtonOnClick() {
        //TODO: Validate các input
        if (productValidator.validate()) {
            Product product = new Product();
            product.setProductCode(txt_productCode.getText());
            product.setName(txt_name.getText());
            product.setPrice(Double.parseDouble(txt_price.getText()));
            product.setStockQuantity(Integer.parseInt(txt_stockQuantity.getText()));
            product.setMonthOfWarranty(Integer.parseInt(txt_monthOfWarranty.getText()));
            product.setNote(txt_note.getText());

            Category selectedCategory = cbb_category.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                product.setCategoryId(selectedCategory.getId());
            }

            Supplier selectedSupplier = cbb_supplier.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) {
                product.setSupplierId(selectedSupplier.getId());
            }

            productService.addProduct(product);
            System.out.println(product);

            // Pass the newly added product to the callback
            if (productAddCallback != null) {
                productAddCallback.onProductAdded(product);
            }

            // Show success message
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();
        }
    }


}
