package vn.aptech.componentmanagementapp.controller.orderdetail;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDetailSelectProductController implements Initializable {
    public interface ProductSelectionCallback {
        void onProductSelected(Product product);
    }
    private ProductSelectionCallback productSelectionCallback;

    public void setProductSelectionCallback(ProductSelectionCallback callback) {
        this.productSelectionCallback = callback;
    }

    //    List
    private ObservableList<Product> products;
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

    // Product Panel
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
    private TableColumn<Product, Integer> tbc_stockQuantity;

    @FXML
    private MFXTextField txt_product_search;
    //Service
    ProductService productService = new ProductService();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        products = FXCollections.observableArrayList(productService.getAllProduct());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(products);
        paginationHelper.setTableView(tableView);

        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        paginationHelper.showFirstPage();

        initEnterKeyPressing();

        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                Product product = tableView.getSelectionModel().getSelectedItem();
                if (productSelectionCallback != null) {
                    productSelectionCallback.onProductSelected(product);
                    stage.close();
                }
            }
        });

    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        tbc_stockQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

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


    private void searchProductOnAction() {
        String searchText = txt_product_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Product> filter = products.stream()
                    .filter(product ->
                            product.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    product.getProductCode().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Product> filterCustomers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterCustomers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(products);
            paginationHelper.showFirstPage();
        }
    }

    private void initEnterKeyPressing() {
        txt_product_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchProductOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_product_search.clear();
                searchProductOnAction();
            }
        });
    }

    @FXML
    void resetFilterIconClicked() {
        txt_product_search.setText("");
        searchProductOnAction();
    }
}
