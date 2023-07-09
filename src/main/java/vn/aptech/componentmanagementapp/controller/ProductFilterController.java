package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
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

    //  Chứa box và label hiển thị trong selected Filter
    @FXML
    private HBox hbox_selectedFilter_categoryGroup;
    @FXML
    private Text lbl_selectedFilter_category;

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

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCategoryFilter();
        initSupplierFilter();

        flowPanel_selectedFilter.getChildren().remove(hbox_selectedFilter_categoryGroup);

//        TODO: Hiển thị selected filter cho Supplier, Price
//        TODO: Lấy giá trị filter để filter tableview
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

        for (ToggleButton toggleButton : categorySelectedToggleButtons) {
            Label label = createLabel(toggleButton.getText(), toggleButton);
            flowPanel_selectedFilter.getChildren().add(label);
        }
    }

    private Label createLabel(String text, ToggleButton toggleButton) {
        Label label = new Label(text);

        FontIcon fontIcon = new FontIcon();
        fontIcon.setIconLiteral("fas-times-circle");
        fontIcon.setIconColor(Paint.valueOf("4A55A2"));

        label.setGraphic(fontIcon);
        label.getStyleClass().addAll("filter-label-selected");
        label.setOnMouseClicked(event -> {
            flowPanel_selectedFilter.getChildren().remove(label);
            toggleButton.setSelected(false);
            categorySelectedToggleButtons.remove(toggleButton);
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
            });
        });

    }

    @FXML
    void viewResultButtonOnClick() {
        tbc_description.setVisible(checkbox_Description.isSelected());
        tbc_note.setVisible(checkbox_note.isSelected());
        tbc_minimumPrice.setVisible(checkbox_minimumPrice.isSelected());

        System.out.println(categorySelectedToggleButtons.size());

        stage.close();
    }
}
