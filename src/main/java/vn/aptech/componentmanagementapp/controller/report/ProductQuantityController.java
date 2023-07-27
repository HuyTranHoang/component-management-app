package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.*;

public class ProductQuantityController implements Initializable {

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
    private Label lbl_totalProduct;

    @FXML
    private Label lbl_totalProductOutOfStock;

    @FXML
    private MFXComboBox<String> cbb_belowQuantity;

    @FXML
    private MFXComboBox<String> cbb_orderBy;

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
        initComboBoxEvent();

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

        List<String> orderList = List.of("ASC", "DESC");
        cbb_orderBy.setItems(FXCollections.observableArrayList(orderList));
        cbb_orderBy.selectFirst();
    }

    private void initComboBoxEvent() {
        EventHandler<ActionEvent> comboBoxEventHandler = event -> {
            String belowQuantity = cbb_belowQuantity.getValue();
            products = switch (belowQuantity) {
                case "Below 10" -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(10));
                case "Out of stock" -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(0));
                default -> FXCollections.observableArrayList(productService.getProductByQuantityBelow(5));
            };

            String order = cbb_orderBy.getValue();
            if (order == null) {
                return;
            }

            if (order.equals("DESC"))
                Collections.reverse(products);

            lbl_totalProduct.setText(String.valueOf(productService.getAllProduct().size()));
            lbl_totalProductOutOfStock.setText(String.valueOf(products.size()));

            paginationHelper.setItems(products);
            showFirstPage();
        };

        cbb_belowQuantity.setOnAction(comboBoxEventHandler);
        cbb_orderBy.setOnAction(comboBoxEventHandler);
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
