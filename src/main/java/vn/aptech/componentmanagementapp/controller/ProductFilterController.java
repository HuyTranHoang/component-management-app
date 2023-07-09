package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.model.Product;

public class ProductFilterController {

    @FXML
    private ToggleGroup tggPrice;

    @FXML
    private MFXCheckbox checkbox_Description;

    @FXML
    private MFXCheckbox checkbox_minimumPrice;

    @FXML
    private MFXCheckbox checkbox_note;

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

    @FXML
    void viewResultButtonOnClick() {
        tbc_description.setVisible(checkbox_Description.isSelected());
        tbc_note.setVisible(checkbox_note.isSelected());
        tbc_minimumPrice.setVisible(checkbox_minimumPrice.isSelected());

        stage.close();
    }

}
