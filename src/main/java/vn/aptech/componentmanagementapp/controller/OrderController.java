package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.service.OrderService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    // Sort and multi deleted
    @FXML
    private MFXButton btn_deleteCheckedOrder;
    @FXML
    private MFXComboBox<String> cbb_orderBy;
    @FXML
    private MFXComboBox<String> cbb_sortBy;

    // Pagination
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
    private TableView<Order> tableView;
    @FXML
    private TableColumn<Order, Long> tbc_id;
    @FXML
    private TableColumn<Order, LocalDateTime> tbc_orderDate;
    @FXML
    private TableColumn<Order, LocalDateTime> tbc_deliveryDate;
    @FXML
    private TableColumn<Order, LocalDateTime> tbc_shipmentDate;
    @FXML
    private TableColumn<Order, String> tbc_deliveryLocation;
    @FXML
    private TableColumn<Order, Double> tbc_totalAmount;
    @FXML
    private TableColumn<Order, String> tbc_note;
    @FXML
    private TableColumn<Order, Long> tbc_customerId;
    @FXML
    private TableColumn<Order, ?> tbc_employeeId;
    @FXML
    private MFXTextField txt_order_search;

    //    List
    private ObservableList<Order> orders;
    private ObservableList<Order> pageItems;

    //  Service
    private OrderService orderService = new OrderService();


    //  Pagination
    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;

    //    Controller to call clear filter function in this
    private OrderAddController orderAddController;

    // Cached views
    private AnchorPane addOrderView;
    @FXML
    private AnchorPane orderView;
    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    /*
     * Begin of Pagination
     */
    private void showPage(int pageIndex) {
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, orders.size());

        pageItems = FXCollections.observableArrayList(orders.subList(startIndex, endIndex));
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
        int maxPageIndex = (int) Math.ceil((double) orders.size() / ITEMS_PER_PAGE) - 1;
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
        int maxPageIndex = (int) Math.ceil((double) orders.size() / ITEMS_PER_PAGE) - 1;
        currentPageIndex = maxPageIndex;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageCount = (int) Math.ceil((double) orders.size() / ITEMS_PER_PAGE);
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
        nextButton.setDisable(currentPageIndex == pageCount - 1);
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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) orders.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    /*
     * End of pagination
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orders = FXCollections.observableArrayList(orderService.getAllOrder());

        initTableView();
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tbc_deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        tbc_shipmentDate.setCellValueFactory(new PropertyValueFactory<>("shipmentDate"));
        tbc_deliveryLocation.setCellValueFactory(new PropertyValueFactory<>("deliveryLocation"));
        tbc_totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        tbc_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        tbc_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tbc_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
    }

    @FXML
    void addButtonOnClick() {
        if (addOrderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order-add.fxml"));
                addOrderView = fxmlLoader.load();
                orderAddController = fxmlLoader.getController();
                orderAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderAddController.setOrderView(orderView);
                orderAddController.setTableView(tableView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

//        productAddController.clearInput();
//        productAddController.addMode();
//
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(addOrderView);
//        productAddController.setRequestFocus();

    }
}
