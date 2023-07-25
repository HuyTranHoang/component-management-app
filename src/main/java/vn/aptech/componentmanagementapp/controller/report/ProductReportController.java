package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ProductReportController implements Initializable {

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

    @FXML
    private MFXComboBox<String> cbb_belowQuantity;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, String> tbc_name;

    @FXML
    private TableColumn<Product, Double> tbc_price;

    @FXML
    private TableColumn<Product, String> tbc_productCode;

    @FXML
    private TableColumn<Product, Integer> tbc_quantity;

    private PaginationHelper<Product> paginationHelper;

    // Service
    private ProductService productService = new ProductService();

    // List
    private ObservableList<Product> products;

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
        List<Product> productList = productService.getProductByQuantityBelow(5);
        products = FXCollections.observableArrayList(productList);
        paginationHelper.setItems(products);
        paginationHelper.showFirstPage();

        initComboBox();

    }

    private void initTableView() {
        tbc_productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        tbc_price.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_price.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPrice()));

        tbc_quantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

    }

    private void initComboBox() {
        List<String> list = List.of("Below 5", "Below 10", "Out of stock");
        cbb_belowQuantity.setItems(FXCollections.observableArrayList(list));
        cbb_belowQuantity.selectFirst();

        cbb_belowQuantity.valueProperty().addListener((observable, oldValue, newValue) -> {
            String belowQuantity = cbb_belowQuantity.getValue();
            products = switch (belowQuantity) {
                case "Below 10" -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(10));
                case "Out of stock" -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(0));
                default -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(5));
            };

            paginationHelper.setItems(products);
            showFirstPage();
        });
    }

    @FXML
    void showFirstPage() {
        paginationHelper.showFirstPage();
    }

    @FXML
    void showLastPage() {
        paginationHelper.showLastPage();
    }

    @FXML
    void showNextPage() {
        paginationHelper.showNextPage();
    }

    @FXML
    void showPreviousPage() {
        paginationHelper.showPreviousPage();
    }

}
