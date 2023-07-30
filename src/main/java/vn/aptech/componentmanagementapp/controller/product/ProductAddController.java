package vn.aptech.componentmanagementapp.controller.product;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import java.util.List;
import java.util.ResourceBundle;

public class ProductAddController implements Initializable {

    // Call back add product
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
    @FXML
    private Label lbl_title;

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

    // Button Group ( Swap mode )
    @FXML
    private HBox hbox_addButtonGroup;
    @FXML
    private HBox hbox_updateButtonGroup;
    @FXML
    private HBox hbox_noti;

    private Timeline timeline;

    // Current Product for edit/update
    private Product currentProduct;
    private TableView<Product> tableView; // Truyền từ Product controller vào

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

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public void setTableView(TableView<Product> tableView) {
        this.tableView = tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Category> categoryList = categoryService.getAllCategory();
            categories = FXCollections.observableArrayList(categoryList);

            List<Supplier> supplierList = supplierService.getAllSupplier();
            suppliers = FXCollections.observableArrayList(supplierList);

            cbb_category.setItems(categories);
            cbb_category.getSelectionModel().selectFirst();
            cbb_supplier.setItems(suppliers);
            cbb_supplier.getSelectionModel().selectFirst();

            initValidator();
            initEnterKeyPressing();
        } catch (Exception e) {
            // Handle any exceptions that occur during data fetching
            e.printStackTrace();
        }
    }


    private void initValidator() {
        productValidator.createCheck()
                .dependsOn("productCode", txt_productCode.textProperty())
                .withMethod(context -> {
                    String productCode = context.get("productCode");
                    if (productCode.isEmpty())
                        context.error("Product code can't be empty");
                    else if (!productCode.matches("([A-Za-z0-9])+"))
                        context.error("Product code can't have whitespace");
                    else if (productCode.length() > 255) {
                        context.error("Product code length exceeds the maximum limit of 255 characters");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_productCode);

        productValidator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String name = context.get("name");
                    if (name.isEmpty())
                        context.error("Name can't be empty");
                    else if (name.length() > 255) {
                        context.error("Name length exceeds the maximum limit of 255 characters");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_name);

        productValidator.createCheck()
                .dependsOn("price", txt_price.textProperty())
                .withMethod(context -> {
                    String price = context.get("price");
                    if (price.isEmpty())
                        context.error("Price can't be empty");
                    else if (!price.matches("\\d+"))
                        context.error("Price can only contain number");
                    else if (Double.parseDouble(price) < 0)
                        context.error("Price need to be greater than 0");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_price);

        productValidator.createCheck()
                .dependsOn("stockQuantity", txt_stockQuantity.textProperty())
                .withMethod(context -> {
                    String stockQuantity = context.get("stockQuantity");
                    if (stockQuantity.isEmpty())
                        context.error("Stock quantity can't be empty");
                    else if (!stockQuantity.matches("\\d+"))
                        context.error("Stock quantity can only contain number");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_stockQuantity);

        productValidator.createCheck()
                .dependsOn("monthOfWarranty", txt_monthOfWarranty.textProperty())
                .withMethod(context -> {
                    String monthOfWarranty = context.get("monthOfWarranty");
                    if (monthOfWarranty.isEmpty())
                        context.error("Month of warranty can't be empty");
                    else if (!monthOfWarranty.matches("\\d+"))
                        context.error("Month of warranty can only contain numbers");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_monthOfWarranty);

        productValidator.createCheck()
                .dependsOn("note", txt_note.textProperty())
                .withMethod(context -> {
                    String note = context.get("note");
                    if (note.length() > 255) {
                        context.error("Note length exceeds the maximum limit of 255 characters");
                    }
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
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/product.fxml"));
                productView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(productView);
    }

    @FXML
    void saveButtonOnClick() {
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

            // Pass the newly added product to the callback
            if (productAddCallback != null) {
                productAddCallback.onProductAdded(product);
            }

            clearInput();

            // Show success message
            lbl_successMessage.setText("Add new product succesfully!!");
            hbox_noti.setVisible(true);
            new FadeInRight(hbox_noti).play();
            timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
            timeline.play();
        }
    }

    void editProduct(Product product) {
        txt_productCode.setText(product.getProductCode());
        txt_name.setText(product.getName());
        txt_price.setText(String.format("%.0f", product.getPrice()));
        txt_stockQuantity.setText(String.valueOf(product.getStockQuantity()));
        txt_monthOfWarranty.setText(String.valueOf(product.getMonthOfWarranty()));
        txt_note.setText(product.getNote());

        Category category = product.getCategory();
        cbb_category.getSelectionModel().selectItem(category);

        Supplier supplier = product.getSupplier();
        cbb_supplier.getSelectionModel().selectItem(supplier);
    }

    @FXML
    void updateButtonOnClick() {
        if (productValidator.validate()) {
            currentProduct.setProductCode(txt_productCode.getText());
            currentProduct.setName(txt_name.getText());
            currentProduct.setPrice(Double.parseDouble(txt_price.getText()));
            currentProduct.setStockQuantity(Integer.parseInt(txt_stockQuantity.getText()));
            currentProduct.setMonthOfWarranty(Integer.parseInt(txt_monthOfWarranty.getText()));
            currentProduct.setNote(txt_note.getText());

            Category selectedCategory = cbb_category.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                currentProduct.setCategoryId(selectedCategory.getId());
                currentProduct.setCategory(selectedCategory);
            }

            Supplier selectedSupplier = cbb_supplier.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) {
                currentProduct.setSupplierId(selectedSupplier.getId());
                currentProduct.setSupplier(selectedSupplier);
            }

            productService.updateProduct(currentProduct);

            // Show success message
            lbl_successMessage.setText("Update product succesfully!!");
            hbox_noti.setVisible(true);
            new FadeInRight(hbox_noti).play();
            timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
            timeline.play();

            int index = tableView.getItems().indexOf(currentProduct);
//            System.out.println(index);
            if (index >= 0) {
                tableView.getItems().set(index, currentProduct);
            }

        }
    }

    @FXML
    void clearInput() { // Được gọi trước khi vào add view ở Product Add Controller
        txt_productCode.clear();
        txt_name.clear();
        txt_price.clear();
        txt_stockQuantity.clear();
        txt_monthOfWarranty.clear();
        txt_note.clear();
        cbb_supplier.selectFirst();
        cbb_category.selectFirst();

        clearValidateError();
    }

    private void clearValidateError() {
        lbl_error_productCode.setVisible(false);
        lbl_error_name.setVisible(false);
        lbl_error_price.setVisible(false);
        lbl_error_stockQuantity.setVisible(false);
        lbl_error_monthOfWarranty.setVisible(false);
    }

    void updateMode() {
        hbox_addButtonGroup.setVisible(false);
        hbox_updateButtonGroup.setVisible(true);

        lbl_title.setText("UPDATE PRODUCT");
    }

    void addMode() {
        hbox_addButtonGroup.setVisible(true);
        hbox_updateButtonGroup.setVisible(false);

        lbl_title.setText("ADD NEW PRODUCT");
    }

    private void initEnterKeyPressing() {
        EventHandler<KeyEvent> storeOrUpdateEventHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (hbox_addButtonGroup.visibleProperty().get()) {
                    saveButtonOnClick();
                } else {
                    updateButtonOnClick();
                }
            } else if (event.getCode() == KeyCode.ESCAPE)
                listProductButtonOnClick();
        };

        txt_productCode.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_name.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_price.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_monthOfWarranty.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_stockQuantity.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_note.setOnKeyPressed(storeOrUpdateEventHandler);
    }

    void setRequestFocus() {
        txt_productCode.requestFocus();
    }

    @FXML
    void hideNoti() {
        timeline.stop();
        new FadeOutRight(hbox_noti).play();
    }


}
