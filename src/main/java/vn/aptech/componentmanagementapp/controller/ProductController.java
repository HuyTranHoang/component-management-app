package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ProductController implements Initializable,
        ProductFilterController.FilterCallback, ProductAddController.ProductAddCallback {

//    Product Panel
    private static ObservableList<Product> products;
    private ObservableList<Product> pageItems;
    private ProductService productService = new ProductService();
    @FXML
    private Label filter_noti_label; // Truyền vào ProductFilterController để set visiable và text
    @FXML
    private Circle filter_noti_shape;
    @FXML
    private MFXTextField txt_product_search;

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
    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;

//    TableView
    @FXML
    private TableView<Product> tableView;
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
    private TableColumn<Product, String> tbc_suppliderId;
    @FXML
    private TableColumn<Product, String> tbc_categoryId;

    private ArrayList<Long> selectedProductIds = new ArrayList<>();

//    Controller to call clear filter function in this
    private ProductFilterController filterController;
    private ProductAddController productAddController;

    // Cached views
    private AnchorPane addProductView;
    @FXML
    private AnchorPane productView;
    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CompletableFuture.supplyAsync(() -> productService.getAllProduct())
                .thenAcceptAsync(productList -> {
                    products = FXCollections.observableArrayList(productList);
                    Platform.runLater(() -> {
                        initTableView();
                        showPage(currentPageIndex);
                        updatePageButtons();
                        initFilterStage();
                        filterController.initSearchListen();

                        initTableViewEvent();
                        initSort();
                    });
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


//        tbc_suppliderId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
//        tbc_categoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));

        tbc_suppliderId.setCellValueFactory(cellData -> {
            Supplier supplier = cellData.getValue().getSupplier();
            if (supplier != null) {
                return new ReadOnlyObjectWrapper<>(supplier.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tbc_categoryId.setCellValueFactory(cellData -> {
            Category category = cellData.getValue().getCategory();
            if (category != null) {
                return new ReadOnlyObjectWrapper<>(category.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        // Checkbox for multi delete
        TableColumn<Product, Boolean> tbc_checkbox = new TableColumn<>("");
        tbc_checkbox.setPrefWidth(40);
        tbc_checkbox.setCellValueFactory(param -> param.getValue().selectedProperty());
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Product, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    TableRow<Product> currentRow = getTableRow();
                    if (currentRow != null) {
                        Product product = currentRow.getItem();
                        if (product != null) {
                            CheckBox checkBox = new CheckBox();
                            checkBox.setSelected(item != null && item);
                            checkBox.setOnAction(event -> {
                                boolean selected = checkBox.isSelected();
                                product.setSelected(selected);
                                if (selected) {
                                    selectedProductIds.add(product.getId());
                                } else {
                                    selectedProductIds.remove(product.getId());
                                }
                            });
                            setGraphic(checkBox);
                        }
                    }
                } else {
                    setGraphic(null);
                }
            }
        });

        tableView.getColumns().add(0, tbc_checkbox);
    }


    private void initTableViewEvent() {
        // Double click thì edit
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                    updateProductButtonOnClick();
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




    /*
     * Begin of Pagination
     */
    private void showPage(int pageIndex) {
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, products.size());

        pageItems = FXCollections.observableArrayList(products.subList(startIndex, endIndex));
        tableView.setItems(pageItems);
    }
    @FXML
    void showPreviousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }
    @FXML
    void showNextPage() {
        int maxPageIndex = (int) Math.ceil((double) products.size() / ITEMS_PER_PAGE) - 1;
        if (currentPageIndex < maxPageIndex) {
            currentPageIndex++;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }
    @FXML
    void showFirstPage() {
        currentPageIndex = 0;
        showPage(currentPageIndex);
        updatePageButtons();
    }
    @FXML
    void showLastPage() {
        int maxPageIndex = (int) Math.ceil((double) products.size() / ITEMS_PER_PAGE) - 1;
        currentPageIndex = maxPageIndex;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageCount = (int) Math.ceil((double) products.size() / ITEMS_PER_PAGE);
        int maxVisibleButtons = 5; // Maximum number of visible page buttons

        int startIndex;
        int endIndex;

        if (pageCount <= maxVisibleButtons) {
            startIndex = 0;
            endIndex = pageCount;
        } else {
            startIndex = Math.max(currentPageIndex - 2, 0);
            endIndex = Math.min(startIndex + maxVisibleButtons, pageCount);

            if (endIndex - startIndex < maxVisibleButtons) {
                startIndex = Math.max(endIndex - maxVisibleButtons, 0);
            }
        }

        pageButtonContainer.getChildren().clear();

        firstPageButton.setDisable(currentPageIndex == 0);
        previousButton.setDisable(currentPageIndex == 0);
        lastPageButton.setDisable(currentPageIndex == pageCount - 1);
        nextButton.setDisable(currentPageIndex == pageCount -1);
        if (startIndex > 0) {
            Button ellipsisButtonStart = new Button("...");
            ellipsisButtonStart.setMinWidth(30);
            ellipsisButtonStart.getStyleClass().add("pagination-button");
            ellipsisButtonStart.setDisable(true);
            pageButtonContainer.getChildren().add(ellipsisButtonStart);
        }

        for (int i = startIndex; i < endIndex; i++) {
            Button pageButton = new Button(Integer.toString(i + 1));
            pageButton.setMinWidth(30);
            pageButton.getStyleClass().add("pagination-button");
            int pageIndex = i;
            pageButton.setOnAction(e -> showPageByIndex(pageIndex));
            pageButtonContainer.getChildren().add(pageButton);

            // Highlight the selected page button
            if (pageIndex == currentPageIndex) {
                pageButton.getStyleClass().add("pagination-button-selected");
            }
        }

        if (endIndex < pageCount) {
            Button ellipsisButtonEnd = new Button("...");
            ellipsisButtonEnd.setMinWidth(30);
            ellipsisButtonEnd.getStyleClass().add("pagination-button");
            ellipsisButtonEnd.setDisable(true);
            pageButtonContainer.getChildren().add(ellipsisButtonEnd);
        }
    }

    private void showPageByIndex(int pageIndex) {
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) products.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    /*
     * End of pagination
     */

    private void initFilterStage() {
        try {
            if (filterScene == null && filterStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product-filter.fxml"));
                filterScene = new Scene(fxmlLoader.load());
                filterStage = new Stage();

                filterController = fxmlLoader.getController();
                filterController.setFilterCallback(this);
                filterController.setStage(filterStage);
                filterController.setTxt_product_search(txt_product_search);
                filterController.setProducts(products);
                filterController.setFilter_noti(filter_noti_shape, filter_noti_label);
                filterController.setProductTable(tableView, tbc_id, tbc_productCode, tbc_name, tbc_price,
                        tbc_quantity, tbc_monthOfWarranty, tbc_note, tbc_suppliderId, tbc_categoryId);

                filterStage.setScene(filterScene);
                filterStage.setResizable(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void filterButtonOnClick() {
        filterStage.show();
    }

    @Override
    public void onFilterApplied(List<Product> filteredProducts) {
        // Update the table view with the filtered products
        products = FXCollections.observableArrayList(filteredProducts);
        showFirstPage();
        updatePageButtons();
    }

    @FXML
    void resetFilterIconClicked() {
        if (filterController != null) {
            filterController.clearFilterButtonOnClick();
        }
        cbb_sortBy.selectFirst();
        cbb_orderBy.selectFirst();
        uncheckAllCheckboxes();
    }

    @FXML
    void addProductButtonOnClick() {
        if (addProductView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product-add.fxml"));
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
    void updateProductButtonOnClick() {
        if (addProductView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product-add.fxml"));
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select product before edit!");
            alert.show();
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
    void deleteProductButtonOnClick() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a product before deleting!");
            alert.show();
        } else {

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected product?");
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                productService.deleteProduct(selectedProduct.getId());
                products.remove(selectedProduct);
                filterController.filterRemoveProduct(selectedProduct);
                tableView.getItems().remove(selectedProduct); // Remove the product from the TableView

                if (pageItems.size() == 0)
                    showPreviousPage();
            }
        }
    }

    @FXML
    void deleteSelectedProductOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + selectedProductIds.size() + " product?");
        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            selectedProductIds.forEach(aLong -> {
                productService.deleteProduct(aLong);
            });

            products = FXCollections.observableArrayList(productService.getAllProduct());
            filterController.setProducts(products);
            showFirstPage();
            updatePageButtons();
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
        // Add the newly added product to the products list
        resetFilterIconClicked(); // Khum có cái này thì bug, chưa hiểu tại sao
        products.add(product); // Nhưng chắc do conflic giữa 2 cái list này
        filterController.filterAddProduct(product); // huhu

        // Update the table view and pagination
        showLastPage();
        updatePageButtons();
    }

}
