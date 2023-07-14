package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OrderController implements Initializable, OrderAddController.OrderAddCallback {
    // Sort and multi deleted
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
    private PaginationHelper<Order> paginationHelper;

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
    private TableColumn<Order, Long> tbc_employeeId;
    @FXML
    private MFXTextField txt_order_search;

    //    List
    private ObservableList<Order> orders;
    private ObservableList<Order> pageItems;

    //  Service
    private final OrderService orderService = new OrderService();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orders = FXCollections.observableArrayList(orderService.getAllOrder());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(orders);
        paginationHelper.setTableView(tableView);

        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        paginationHelper.showFirstPage();
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        tbc_orderDate.setCellFactory(formatCellFactory(formatter));
        tbc_deliveryDate.setCellFactory(formatCellFactory(formatter));
        tbc_shipmentDate.setCellFactory(formatCellFactory(formatter));
    }

    private Callback<TableColumn<Order, LocalDateTime>, TableCell<Order, LocalDateTime>> formatCellFactory(DateTimeFormatter formatter) {
        return column -> {
            TableCell<Order, LocalDateTime> cell = new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
            return cell;
        };
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

                orderAddController.setOrderAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        orderAddController.clearInput();
        orderAddController.addMode();

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(addOrderView);
//        productAddController.setRequestFocus();
    }

    @FXML
    void editButtonOnClick() {
        if (addOrderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order-add.fxml"));
                addOrderView = fxmlLoader.load();
                orderAddController = fxmlLoader.getController();
                orderAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderAddController.setOrderView(orderView);
                orderAddController.setTableView(tableView);

                orderAddController.setOrderAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select product before edit!");
            alert.show();
        } else {
            orderAddController.clearInput();
            orderAddController.updateMode();
            orderAddController.editOrder(selectedOrder);
            orderAddController.setCurrentOrder(selectedOrder);

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(addOrderView);
//            orderAddController.setRequestFocus();

        }

    }

    @Override
    public void onOrderAdded(Order order) {
        orders.add(order);
        showLastPage();
    }
}
