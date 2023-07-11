package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.CategoryService;
import vn.aptech.componentmanagementapp.service.SupplierService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProductFilterController implements Initializable {

    // Callback dùng để truyền list product đã được filter ra ProductController
    public interface FilterCallback {
        void onFilterApplied(List<Product> filteredProducts);
    }
    private FilterCallback filterCallback;
    public void setFilterCallback(FilterCallback filterCallback) {
        this.filterCallback = filterCallback;
    }
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
    private TableColumn<Product, Integer> tbc_quantity;
    @FXML
    private TableColumn<Product, Integer> tbc_monthOfWarranty;
    @FXML
    private TableColumn<Product, String> tbc_note;
    @FXML
    private TableColumn<Product, Long> tbc_suppliderId;
    @FXML
    private TableColumn<Product, Long> tbc_categoryId;

//    List product từ product controller
    private static ObservableList<Product> products;
    private List<Product> filteredProducts = null;

//    Debound for search text field
    private Timer debounceTimer;
    private boolean isInputPending = false;
    private final long DEBOUNCE_DELAY = 1000; // Delay in milliseconds


//    Dùng để gán action - Được truyền vào từ ProductController
    private Stage stage;
    @FXML
    private MFXTextField txt_product_search;
    @FXML
    private Label filter_noti_label; // Truyền vào ProductController để set visiable và text
    @FXML
    private Circle filter_noti_shape;

//    Hàm set

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTxt_product_search(MFXTextField txt_product_search) {
        this.txt_product_search = txt_product_search;
    }

    public void setProducts(ObservableList<Product> products) {
        ProductFilterController.products = products;
    }

    public void setFilter_noti(Circle filter_noti_shape, Label filter_noti_label) {
        this.filter_noti_shape = filter_noti_shape;
        this.filter_noti_label = filter_noti_label;
    }
    public void setProductTable(TableView<Product> tableView, TableColumn<Product, Long> tbc_id,
                                TableColumn<Product, String> tbc_productCode, TableColumn<Product, String> tbc_name,
                                TableColumn<Product, Double> tbc_price,
                                TableColumn<Product, Integer> tbc_quantity, TableColumn<Product, Integer> tbc_monthOfWarranty,
                                TableColumn<Product, String> tbc_note,
                                TableColumn<Product, Long> tbc_suppliderId, TableColumn<Product, Long> tbc_categoryId) {
        this.tableView = tableView;
        this.tbc_id = tbc_id;
        this.tbc_productCode = tbc_productCode;
        this.tbc_name = tbc_name;
        this.tbc_price = tbc_price;
        this.tbc_quantity = tbc_quantity;
        this.tbc_monthOfWarranty = tbc_monthOfWarranty;
        this.tbc_note = tbc_note;
        this.tbc_suppliderId = tbc_suppliderId;
        this.tbc_categoryId = tbc_categoryId;
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
        int countFilter = 0; // Dùng để đếm số lượng filter xong gán thành text cho noti

        // Checkbox
//        TODO: thêm những cột khác có thể hiện/ ẩn
//        TODO: Sửa category id v supplier id thành tên trong table view
        tbc_note.setVisible(checkbox_note.isSelected());

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

            countFilter++;
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
            countFilter = countFilter + selectedCategoryIds.size();
        }

        // Supplier
        List<Long> selectedSupplierIds = supplierSelectedToggleButtons.stream()
                .map(toggleButton -> (Long) toggleButton.getUserData())
                .toList();

        if (!selectedSupplierIds.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> selectedSupplierIds.contains(product.getSupplierId()))
                    .collect(Collectors.toList());
            countFilter = countFilter + selectedSupplierIds.size();
        }

        // Search
        String searchText = txt_product_search.getText().trim();
        if (!searchText.isEmpty()) {
            filteredProducts = filteredProducts.stream()
                    .filter(product ->
                            product.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    product.getProductCode().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (countFilter != 0) {
            filter_noti_shape.setVisible(true);
            filter_noti_label.setText(String.valueOf(countFilter));
            filter_noti_label.setVisible(true);
        } else {
            filter_noti_shape.setVisible(false);
            filter_noti_label.setVisible(false);
        }

        if (filterCallback != null) {
            filterCallback.onFilterApplied(filteredProducts);
        }

        stage.close();
    }

    @FXML
    void clearFilterButtonOnClick() {
        checkbox_minimumPrice.setSelected(false);
        checkbox_note.setSelected(false);
        checkbox_Description.setSelected(false);

        supplierSelectedToggleButtons.forEach(toggleButton -> toggleButton.setSelected(false));
        supplierSelectedToggleButtons.clear();

        categorySelectedToggleButtons.forEach(toggleButton -> toggleButton.setSelected(false));
        categorySelectedToggleButtons.clear();

        if (tggPrice.getSelectedToggle() != null)
            tggPrice.getSelectedToggle().setSelected(false);

        updateSelectedButtonsLabel(); // Clear the existing labels

        txt_product_search.clear(); // Clear search text

        viewResultButtonOnClick();

        stage.close();
    }

    void initSearchListen() {

        txt_product_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                viewResultButtonOnClick();
            else if (event.getCode() == KeyCode.ESCAPE) {
                txt_product_search.clear();
                viewResultButtonOnClick();
            }
        });

        txt_product_search.textProperty().addListener((observable, oldValue, newValue) -> {
            // Clear the previous timer if it exists
            if (debounceTimer != null) {
                debounceTimer.cancel();
            }

            // Set the input pending flag to true
            isInputPending = true;

            // Create a new timer
            debounceTimer = new Timer();
            debounceTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Check if there is a pending input
                    if (isInputPending) {
                        Platform.runLater(() -> {
                            // Call the viewResultButtonOnClick method
                            viewResultButtonOnClick();
                            // Set the input pending flag to false
                            isInputPending = false;
                        });
                    }
                }
            }, DEBOUNCE_DELAY);
        });
    }

}
