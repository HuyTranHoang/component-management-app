package vn.aptech.componentmanagementapp.controller.product;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;
import vn.aptech.componentmanagementapp.util.SetImageAlert;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable,
        ProductFilterController.FilterCallback, ProductAddController.ProductAddCallback, ProductFilterController.ClearFilterCallback {

//    Product Panel
    private ObservableList<Product> products;
    private final ProductService productService = new ProductService();
    @FXML
    private Label filter_noti_label; // Truyền vào ProductFilterController để set visiable và text
    @FXML
    private Circle filter_noti_shape;
    @FXML
    private MFXTextField txt_product_search;
    @FXML
    private HBox hbox_addEditDelete;
    @FXML
    private HBox hbox_confirmDelete;
    @FXML
    private HBox hbox_noti;

    private Timeline timeline;


//    Filter Panel
    private Scene filterScene;
    private Stage filterStage;
//    Sort
    @FXML
    private MFXComboBox<String> cbb_sortBy;
    @FXML
    private MFXComboBox<String> cbb_orderBy;

//  Pagination
    @FXML
    private Button firstPageButton;
    @FXML
    private Button lastPageButton;
    @FXML
    private Button nextButton;
    @FXML
    private HBox pageButtonContainer;
    @FXML
    private Button previousButton;
    private PaginationHelper<Product> paginationHelper;

//    TableView
    @FXML
    private TableView<Product> tableView;
    @FXML
    private TableColumn<Product, Boolean> tbc_checkbox;
    @FXML
    private TableColumn<Product, Long> tbc_id;
    @FXML
    private TableColumn<Product, String> tbc_productCode;
    @FXML
    private TableColumn<Product, String> tbc_name;
    @FXML
    private TableColumn<Product, Double> tbc_price;
    @FXML
    private TableColumn<Product, Integer> tbc_quantity;
    @FXML
    private TableColumn<Product, Integer> tbc_monthOfWarranty;
    @FXML
    private TableColumn<Product, String> tbc_note;
    @FXML
    private TableColumn<Product, Supplier> tbc_supplierId;
    @FXML
    private TableColumn<Product, Category> tbc_categoryId;

    private final ArrayList<Long> selectedProductIds = new ArrayList<>();

//    Controller to call clear filter function in this
    private ProductFilterController filterController;
    private ProductAddController productAddController;

    // Cached views
    private AnchorPane addProductView;
    @FXML
    private AnchorPane productView;

    public void setProductView(AnchorPane productView) {
        this.productView = productView;
    }

    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paginationHelper = new PaginationHelper<>();
        initTableView();

        paginationHelper.setTableView(tableView);
        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        Platform.runLater(() -> {
            List<Product> productList = productService.getAllProduct();
            products = FXCollections.observableArrayList(productList);

            paginationHelper.setItems(products);
            paginationHelper.showFirstPage();
            initFilterStage();
            filterController.initSearchListen();
            initTableViewEvent();
            initSort();
        });
    }

    private void initTableView() {

        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        tbc_price.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_price.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPrice()));

        tbc_quantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        tbc_monthOfWarranty.setCellValueFactory(new PropertyValueFactory<>("monthOfWarranty"));
        tbc_note.setCellValueFactory(new PropertyValueFactory<>("note"));

        tbc_supplierId.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        tbc_categoryId.setCellValueFactory(new PropertyValueFactory<>("category"));


        initCheckBox();
    }

    private void initCheckBox() {
        tbc_checkbox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Product, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Product product = getTableRow().getItem();
                    boolean selected = checkBox.isSelected();
                    product.setSelected(selected);
                    if (selected) {
                        selectedProductIds.add(product.getId());
                    } else {
                        selectedProductIds.remove(product.getId());
                    }
                    updateRowStyle();
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                    updateRowStyle();
                }
            }

            private void updateRowStyle() {
                boolean selected = checkBox.isSelected();
                TableRow<Product> currentRow = getTableRow();
                if (currentRow != null) {
                    currentRow.setStyle(selected ? "-fx-background-color: #ffb8b4;" : "");
                }
            }
        });

    }

    private void initTableViewEvent() {
        // Double click thì edit
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                    editButtonOnClick();
            }
        });
    }

    private void initSort() {
        cbb_sortBy.setItems(FXCollections.observableArrayList(List.of("Id", "Name", "Price", "Quantity")));
        cbb_orderBy.setItems(FXCollections.observableArrayList(List.of("ASC", "DESC")));
        // Add listeners to both ComboBoxes
        cbb_sortBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
        cbb_orderBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
    }

    private void applySorting() {
        String sortBy = cbb_sortBy.getValue();
        String orderBy = cbb_orderBy.getValue();
        Comparator<Product> comparator = switch (sortBy) {
            case "Name" -> Comparator.comparing(Product::getName);
            case "Price" -> Comparator.comparing(Product::getPrice);
            case "Quantity" -> Comparator.comparing(Product::getStockQuantity);
            default -> Comparator.comparing(Product::getId);
        };
        // Check the selected value of cbb_orderBy and adjust the comparator accordingly
        if ("DESC".equals(orderBy)) {
            comparator = comparator.reversed();
        }
        // Sort the products list with the chosen comparator
        FXCollections.sort(products, comparator);
        showFirstPage();
    }


    @FXML
    void showPreviousPage() {
        paginationHelper.showPreviousPage();
    }
    @FXML
    void showNextPage() {
        paginationHelper.showNextPage();
    }
    @FXML
    void showFirstPage() {
        paginationHelper.showFirstPage();
    }
    @FXML
    void showLastPage() {
        paginationHelper.showLastPage();
    }

    private void initFilterStage() {
        try {
            if (filterScene == null && filterStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/product-filter.fxml"));
                filterScene = new Scene(fxmlLoader.load());
                filterStage = new Stage();

                filterStage.setTitle("Filter Product");

                Image image = null;

                URL resourceURL = ComponentManagementApplication.class.getResource("images/product.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }

                filterStage.getIcons().add(image);

                filterStage.initModality(Modality.APPLICATION_MODAL);

                filterController = fxmlLoader.getController();

                filterController.setStage(filterStage);
                filterController.setTxt_product_search(txt_product_search);
                filterController.setProducts(products);
                filterController.setFilter_noti(filter_noti_shape, filter_noti_label);
                filterController.setProductTable(tbc_monthOfWarranty, tbc_note);

                filterStage.setScene(filterScene);
                filterStage.setResizable(false);

                filterController.setFilterCallback(this);
                filterController.setClearFilterCallback(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void filterButtonOnClick() {
        filterController.setProducts(products);
        filterController.reloadFilter();
        filterStage.show();
    }

    @Override
    public void onFilterApplied(List<Product> filteredProducts) {
        // Update the table view with the filtered products
        products = FXCollections.observableArrayList(filteredProducts);
        paginationHelper.setItems(products);
        showFirstPage();
    }

    @FXML
    public void resetFilterIconClicked() {
        if (filterController != null) {
            filterController.clearFilterButtonOnClick();
        }

        cbb_sortBy.selectFirst();
        cbb_orderBy.selectFirst();

        products = FXCollections.observableArrayList(productService.getAllProduct());
        paginationHelper.setItems(products);

        uncheckAllCheckboxes();
    }

    @FXML
    void addButtonOnClick() {
        if (addProductView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/product-add.fxml"));
                addProductView = fxmlLoader.load();
                productAddController = fxmlLoader.getController();
                productAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                productAddController.setProductView(productView);
                productAddController.setTableView(tableView);

                // Pass the products list and set the ProductAddCallback
                productAddController.setProductAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        productAddController.clearInput();
        productAddController.addMode();

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(addProductView);
        productAddController.setRequestFocus();

    }

    @FXML
    void editButtonOnClick() {
        if (addProductView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/product-add.fxml"));
                addProductView = fxmlLoader.load();
                productAddController = fxmlLoader.getController();
                productAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                productAddController.setProductView(productView);
                productAddController.setTableView(tableView);

                // Pass the products list and set the ProductAddCallback
                productAddController.setProductAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Please select product before edit!");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {
            productAddController.clearInput();
            productAddController.updateMode();
            productAddController.editProduct(selectedProduct);
            productAddController.setCurrentProduct(selectedProduct);

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(addProductView);
            productAddController.setRequestFocus();
        }

    }


    @FXML
    void deleteContextOnClick() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText(null);
            error.setContentText("Please select a product before deleting!");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected product?");

            SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                productService.deleteProduct(selectedProduct.getId());
                products.remove(selectedProduct);
                filterController.filterRemoveProduct(selectedProduct);
                tableView.getItems().remove(selectedProduct); // Remove the product from the TableView
                tableView.refresh();

                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();

                hbox_noti.setVisible(true);
                new FadeInRight(hbox_noti).play();
                timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
                timeline.play();
            }
        }
    }

    @FXML
    void deleteButtonOnClick() {
        hbox_addEditDelete.setVisible(false);
        hbox_confirmDelete.setVisible(true);

        tbc_checkbox.setVisible(true);
    }

    @FXML
    void backButtonOnClick() {
        hbox_addEditDelete.setVisible(true);
        hbox_confirmDelete.setVisible(false);

        tbc_checkbox.setVisible(false);

        uncheckAllCheckboxes();
        tableView.refresh();
    }


    @FXML
    void deleteSelectedProductOnClick() {
        if (selectedProductIds.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Confirm");
            error.setHeaderText(null);
            error.setContentText("Please select checkbox product you want to delete.");

            SetImageAlert.setIconAlert(error, SetImageAlert.ERROR);
            error.show();
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete " + selectedProductIds.size() + " products?");

            SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                selectedProductIds.forEach(aLong -> {
                    productService.deleteProduct(aLong);
                    Product product = products.stream()
                            .filter(p -> p.getId() == aLong)
                            .findFirst()
                            .orElse(null);

                    products.remove(product);
//                    filterController.filterRemoveProduct(product);
                    paginationHelper.getPageItems().remove(product);
                });

//            products = FXCollections.observableArrayList(productService.getAllProduct());
//            filterController.setProducts(products);
//            resetFilterIconClicked();
                hbox_noti.setVisible(true);
                new FadeInRight(hbox_noti).play();
                timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOutRight(hbox_noti).play()));
                timeline.play();

                uncheckAllCheckboxes();
                paginationHelper.showCurrentPage();
                tableView.refresh();
            }
        }

    }

    private void uncheckAllCheckboxes() {
        for (Product product : tableView.getItems()) {
            product.setSelected(false);
        }
        selectedProductIds.clear();
    }

    @Override
    public void onProductAdded(Product product) {
        products.add(product);
        showLastPage();
    }

    public void reloadProduct() {
        products = FXCollections.observableArrayList(productService.getAllProduct());
        paginationHelper.setItems(products);
//        showFirstPage();
        paginationHelper.showCurrentPage();
    }

    @Override
    public void onClearFilterClicked() {
        products = FXCollections.observableArrayList(productService.getAllProduct());
        paginationHelper.setItems(products);
//        showFirstPage();
        paginationHelper.showCurrentPage();
        uncheckAllCheckboxes();
    }

    @FXML
    void hideNoti() {
        timeline.stop();
        new FadeOutRight(hbox_noti).play();
    }
}
