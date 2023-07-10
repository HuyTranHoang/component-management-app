package vn.aptech.componentmanagementapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

//    Product Panel
    private static ObservableList<Product> products;

    private ObservableList<Product> pageItems;
    private ProductService productService = new ProductService();

    @FXML
    private FontIcon resetFilterIcon; // Truyền vào ProductController để gán on click
    @FXML
    private Label filter_noti_label; // Truyền vào ProductController để set visiable và text
    @FXML
    private Circle filter_noti_shape;

//    Filter Panel
    private Scene filterScene;
    private Stage filterStage;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        products = FXCollections.observableArrayList(productService.getAllProduct());

        initTableView();
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        tbc_price.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_price.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPrice()));

        tbc_minimumPrice.setCellValueFactory(new PropertyValueFactory<>("minimumPrice"));
        tbc_quantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        tbc_monthOfWarranty.setCellValueFactory(new PropertyValueFactory<>("monthOfWarranty"));
        tbc_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        tbc_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tbc_suppliderId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        tbc_categoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
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

    @FXML
    void filterButtonOnClick() {
        try {
            if (filterScene == null && filterStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("main-product-filter.fxml"));
                filterScene = new Scene(fxmlLoader.load());
                filterStage = new Stage();

                ProductFilterController controller = fxmlLoader.getController();
                controller.setStage(filterStage);
                controller.setProducts(products);
                controller.setPageItems(pageItems);
                controller.setResetFilterIcon(resetFilterIcon);
                controller.setFilter_noti(filter_noti_shape, filter_noti_label);
                controller.setPaginationButton(firstPageButton, previousButton, nextButton, lastPageButton, pageButtonContainer);
                controller.setProductTable(tableView, tbc_id, tbc_productCode, tbc_name, tbc_price, tbc_minimumPrice,
                        tbc_quantity, tbc_monthOfWarranty, tbc_note, tbc_description, tbc_suppliderId, tbc_categoryId);

                filterStage.setScene(filterScene);
                filterStage.setResizable(false);
            }

            filterStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
