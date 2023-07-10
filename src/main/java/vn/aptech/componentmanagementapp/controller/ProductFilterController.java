package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.CategoryService;
import vn.aptech.componentmanagementapp.service.SupplierService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProductFilterController implements Initializable {

    //    Dùng để filter những cột nào hiển thị trên table
    @FXML
    private ToggleGroup tggPrice;
    @FXML
    private MFXCheckbox checkbox_Description;
    @FXML
    private MFXCheckbox checkbox_minimumPrice;
    @FXML
    private MFXCheckbox checkbox_note;

    //    Chứa toggle button để filter category
    @FXML
    private FlowPane flowPanel_category;
    @FXML
    private FlowPane flowPanel_supplier;
    @FXML
    private FlowPane flowPanel_selectedFilter;

    //  Chứa data được lấy từ database
    private ObservableList<Category> categories;
    private ObservableList<Supplier> suppliers;
    //  Chứa danh sách những nút được selected ( Khi án thì sẽ add vào list này )
    List<ToggleButton> categorySelectedToggleButtons = new ArrayList<>();
    List<ToggleButton> supplierSelectedToggleButtons = new ArrayList<>();
    //    Service
    private CategoryService categoryService = new CategoryService();
    private SupplierService supplierService = new SupplierService();
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
    private TableColumn<Product, Double> tbc_minimumPrice;
    @FXML
    private TableColumn<Product, Integer> tbc_quantity;
    @FXML
    private TableColumn<Product, Integer> tbc_monthOfWarranty;
    @FXML
    private TableColumn<Product, String> tbc_note;
    @FXML
    private TableColumn<Product, String> tbc_description;
    @FXML
    private TableColumn<Product, Long> tbc_suppliderId;
    @FXML
    private TableColumn<Product, Long> tbc_categoryId;

//    List product từ product controller
    private static ObservableList<Product> products;
    private List<Product> filteredProducts = null;
    private ObservableList<Product> pageItems;
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

//    Pagination
    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;
    private Stage stage;

//    Dùng để gán action
    @FXML
    private FontIcon resetFilterIcon;

//    Hàm set

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProducts(ObservableList<Product> products) {
        ProductFilterController.products = products;
    }

    public void setPageItems(ObservableList<Product> pageItems) {
        this.pageItems = pageItems;
    }

    public void setResetFilterIcon(FontIcon resetFilterIcon) {
        this.resetFilterIcon = resetFilterIcon;

        this.resetFilterIcon.setOnMouseClicked(event -> clearFilterButtonOnClick());
    }

    public void setProductTable(TableView<Product> tableView, TableColumn<Product, Long> tbc_id,
                                TableColumn<Product, String> tbc_productCode, TableColumn<Product, String> tbc_name,
                                TableColumn<Product, Double> tbc_price, TableColumn<Product, Double> tbc_minimumPrice,
                                TableColumn<Product, Integer> tbc_quantity, TableColumn<Product, Integer> tbc_monthOfWarranty,
                                TableColumn<Product, String> tbc_note, TableColumn<Product, String> tbc_description,
                                TableColumn<Product, Long> tbc_suppliderId, TableColumn<Product, Long> tbc_categoryId) {
        this.tableView = tableView;
        this.tbc_id = tbc_id;
        this.tbc_productCode = tbc_productCode;
        this.tbc_name = tbc_name;
        this.tbc_price = tbc_price;
        this.tbc_minimumPrice = tbc_minimumPrice;
        this.tbc_quantity = tbc_quantity;
        this.tbc_monthOfWarranty = tbc_monthOfWarranty;
        this.tbc_note = tbc_note;
        this.tbc_description = tbc_description;
        this.tbc_suppliderId = tbc_suppliderId;
        this.tbc_categoryId = tbc_categoryId;
    }

    public void setPaginationButton(Button firstPageButton, Button previousButton, Button nextButton, Button lastPageButton, HBox pageButtonContainer) {
        this.firstPageButton = firstPageButton;
        this.previousButton = previousButton;
        this.nextButton = nextButton;
        this.lastPageButton = lastPageButton;
        this.pageButtonContainer = pageButtonContainer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCategoryFilter();
        initSupplierFilter();

        initEventForRadioGroup();
    }

    private void initCategoryFilter() {
        categories = FXCollections.observableArrayList(categoryService.getAllCategory());

        categories.forEach(category -> {
            ToggleButton toggleButton = new ToggleButton();
            toggleButton.setText(category.getName());
            toggleButton.setUserData(category.getId()); // Associate the ID with the toggle button
            toggleButton.getStyleClass().add("filter-toggle-button");
            flowPanel_category.getChildren().add(toggleButton);

            toggleButton.setOnAction(event -> {
                if (toggleButton.isSelected()) {
                    categorySelectedToggleButtons.add(toggleButton);
                } else {
                    categorySelectedToggleButtons.remove(toggleButton);
                }

                updateSelectedButtonsLabel();
            });
        });
    }

    private void updateSelectedButtonsLabel() {
        flowPanel_selectedFilter.getChildren().clear(); // Clear the existing labels

        addLabelsForSelectedToggleButtons(categorySelectedToggleButtons);
        addLabelsForSelectedToggleButtons(supplierSelectedToggleButtons);
        addLabelsForSelectedToggleButtons(getSelectedRadioButton(tggPrice));
    }

    private List<ToggleButton> getSelectedRadioButton(ToggleGroup toggleGroup) {
        List<ToggleButton> selectedToggleButtons = new ArrayList<>();

        if (toggleGroup.getSelectedToggle() != null) {
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            selectedToggleButtons.add(selectedRadioButton);
        }

        return selectedToggleButtons;
    }

    private void addLabelsForSelectedToggleButtons(List<ToggleButton> selectedToggleButtons) {
        for (ToggleButton toggleButton : selectedToggleButtons) {
            Label label = createLabel(toggleButton.getText(), toggleButton, selectedToggleButtons);
            flowPanel_selectedFilter.getChildren().add(label);
        }
    }

    private Label createLabel(String text, ToggleButton toggleButton, List<ToggleButton> selectedToggleButtons) {
        Label label = new Label(text);

        FontIcon fontIcon = new FontIcon();
        fontIcon.setIconLiteral("fas-times-circle");
        fontIcon.setIconColor(Paint.valueOf("4A55A2"));

        label.setGraphic(fontIcon);
        label.getStyleClass().addAll("filter-label-selected");
        label.setOnMouseClicked(event -> {
            flowPanel_selectedFilter.getChildren().remove(label);
            toggleButton.setSelected(false);
            selectedToggleButtons.remove(toggleButton);
//            supplierSelectedToggleButtons.remove(toggleButton);
        });
        return label;
    }


    private void initSupplierFilter() {
        suppliers = FXCollections.observableArrayList(supplierService.getAllSupplier());

        suppliers.forEach(supplier -> {
            ToggleButton toggleButton = new ToggleButton();
            toggleButton.setText(supplier.getName());
            toggleButton.setUserData(supplier.getId()); // Dùng để lưu trữ id, khi cần dùng thì toggleButton.getUserData();
            toggleButton.getStyleClass().add("filter-toggle-button");
            flowPanel_supplier.getChildren().add(toggleButton);

            toggleButton.setOnAction(event -> {
                if (toggleButton.isSelected()) {
                    supplierSelectedToggleButtons.add(toggleButton);
                } else {
                    supplierSelectedToggleButtons.remove(toggleButton);
                }

                updateSelectedButtonsLabel();
            });
        });
    }

    private void initEventForRadioGroup() {
        tggPrice.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                updateSelectedButtonsLabel();
            }
        });
    }

    @FXML
    void viewResultButtonOnClick() {
        // Checkbox
        tbc_description.setVisible(checkbox_Description.isSelected());
        tbc_note.setVisible(checkbox_note.isSelected());
        tbc_minimumPrice.setVisible(checkbox_minimumPrice.isSelected());

        // Radio
        if (tggPrice.getSelectedToggle() != null) {
            RadioButton selectedRadio = (RadioButton) tggPrice.getSelectedToggle();
            String selectedPrice = selectedRadio.getText();

            filteredProducts = switch (selectedPrice) {
                case "Below 2,000,000" -> products.stream()
                        .filter(product -> product.getPrice() < 2000000)
                        .collect(Collectors.toList());
                case "2,000,0000 - 5,000,000" -> products.stream()
                        .filter(product -> product.getPrice() >= 2000000 && product.getPrice() <= 5000000)
                        .collect(Collectors.toList());
                case "5,000,0000 - 10,000,000" -> products.stream()
                        .filter(product -> product.getPrice() >= 5000000 && product.getPrice() <= 10000000)
                        .collect(Collectors.toList());
                case "10,000,000 - 20,000,000" -> products.stream()
                        .filter(product -> product.getPrice() >= 10000000 && product.getPrice() <= 20000000)
                        .collect(Collectors.toList());
                default -> products.stream()
                        .filter(product -> product.getPrice() > 20000000)
                        .collect(Collectors.toList());
            };
        } else {
            filteredProducts = new ArrayList<>(products); // Original list
        }

        // Category
        List<Long> selectedCategoryIds = categorySelectedToggleButtons.stream()
                .map(toggleButton -> (Long) toggleButton.getUserData())
                .toList();

        if (!selectedCategoryIds.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> selectedCategoryIds.contains(product.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // Supplier
        List<Long> selectedSupplierIds = supplierSelectedToggleButtons.stream()
                .map(toggleButton -> (Long) toggleButton.getUserData())
                .toList();

        if (!selectedSupplierIds.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> selectedSupplierIds.contains(product.getSupplierId()))
                    .collect(Collectors.toList());
        }

        showFirstPage();
        updatePageButtons();

        stage.close();
    }

    @FXML
    void clearFilterButtonOnClick() {
        supplierSelectedToggleButtons.forEach(toggleButton -> toggleButton.setSelected(false));
        supplierSelectedToggleButtons.clear();

        categorySelectedToggleButtons.forEach(toggleButton -> toggleButton.setSelected(false));
        categorySelectedToggleButtons.clear();

        if (tggPrice.getSelectedToggle() != null)
            tggPrice.getSelectedToggle().setSelected(false);

        updateSelectedButtonsLabel(); // Clear the existing labels

        viewResultButtonOnClick();

        stage.close();
    }

    /*
     * Begin of Pagination
     */
    private void showPage(int pageIndex) {
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredProducts.size());

        pageItems = FXCollections.observableArrayList(filteredProducts.subList(startIndex, endIndex));
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
        int maxPageIndex = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE) - 1;
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
        int maxPageIndex = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE) - 1;
        currentPageIndex = maxPageIndex;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageCount = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    /*
     * End of pagination
     */


}
