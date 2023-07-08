package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ProductController implements Initializable {


    @FXML
    private MFXPaginatedTableView<Product> product_pagin_tableView;

    private ArrayList<Product> products;

    private ProductService productService = new ProductService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        products = (ArrayList<Product>) productService.getAllProduct();
        setupPaginated();
    }

    private void setupPaginated() {
        MFXTableColumn<Product> idColumn = new MFXTableColumn<>("Id", false, null);
        MFXTableColumn<Product> productCodeColumn = new MFXTableColumn<>("Product Code", false, null);
        MFXTableColumn<Product> nameColumn = new MFXTableColumn<>("Name", false, null);
        MFXTableColumn<Product> priceColumn = new MFXTableColumn<>("Price", false, null);
        MFXTableColumn<Product> minimumPriceColumn = new MFXTableColumn<>("Minimum price", false, null);
        MFXTableColumn<Product> stockQuanttiyColumn = new MFXTableColumn<>("Stock quantity", false, null);
        MFXTableColumn<Product> monthOfWarrantyColumn = new MFXTableColumn<>("Month of warranty", false, null);
        MFXTableColumn<Product> noteColumn = new MFXTableColumn<>("Note", false, null);
        MFXTableColumn<Product> descriptionColumn = new MFXTableColumn<>("Description", false, null);
        MFXTableColumn<Product> supplierIdColumn = new MFXTableColumn<>("SupplierId", false, null);
        MFXTableColumn<Product> categoryIdColumn = new MFXTableColumn<>("CategoryId", false, null);


        idColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getId));
        productCodeColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getProductCode));
        nameColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getName));
        priceColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getPrice));
        minimumPriceColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getMinimumPrice));
        stockQuanttiyColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getStockQuantity));
        monthOfWarrantyColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getMonthOfWarranty));
        noteColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getNote));
        descriptionColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getDescription));
        supplierIdColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getSupplierId));
        categoryIdColumn.setRowCellFactory(product -> new MFXTableRowCell<>(Product::getSupplierId));


        product_pagin_tableView.getTableColumns().addAll(idColumn, productCodeColumn, nameColumn, priceColumn,
                minimumPriceColumn, stockQuanttiyColumn, monthOfWarrantyColumn, noteColumn, descriptionColumn,
                supplierIdColumn, categoryIdColumn);

        product_pagin_tableView.setItems(FXCollections.observableArrayList(products));
    }

}
