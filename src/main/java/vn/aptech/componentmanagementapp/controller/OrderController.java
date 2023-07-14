package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private TableColumn<Order, Boolean> tbc_checkbox;
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
    private TableColumn<Order, String> tbc_customerId;
    @FXML
    private TableColumn<Order, String> tbc_employeeId;
    @FXML
    private MFXTextField txt_order_search;

    private final ArrayList<Long> selectedOrderIds = new ArrayList<>();
    @FXML
    private HBox hbox_addEditDelete;
    @FXML
    private HBox hbox_confirmDelete;
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

        initEnterKeyPressing();
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tbc_deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        tbc_shipmentDate.setCellValueFactory(new PropertyValueFactory<>("shipmentDate"));
        tbc_deliveryLocation.setCellValueFactory(new PropertyValueFactory<>("deliveryLocation"));
        tbc_totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        tbc_note.setCellValueFactory(new PropertyValueFactory<>("note"));
//        tbc_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
//        tbc_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        tbc_customerId.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue().getCustomer();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tbc_employeeId.setCellValueFactory(cellData -> {
            Employee employee = cellData.getValue().getEmployee();
            if (employee != null) {
                return new ReadOnlyObjectWrapper<>(employee.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        tbc_orderDate.setCellFactory(formatCellFactory(formatter));
        tbc_deliveryDate.setCellFactory(formatCellFactory(formatter));
        tbc_shipmentDate.setCellFactory(formatCellFactory(formatter));

        initCheckBox();
    }

    private void initCheckBox() {
        tbc_checkbox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Order, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Order order = getTableRow().getItem();
                    boolean selected = checkBox.isSelected();
                    order.setSelected(selected);
                    if (selected) {
                        selectedOrderIds.add(order.getId());
                    } else {
                        selectedOrderIds.remove(order.getId());
                    }
                    updateRowStyle();
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                    updateRowStyle();
                }
            }

            private void updateRowStyle() {
                boolean selected = checkBox.isSelected();
                TableRow<Order> currentRow = getTableRow();
                if (currentRow != null) {
                    currentRow.setStyle(selected ? "-fx-background-color: #ffb8b4;" : "");
                }
            }
        });

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

//        orderAddController.clearInput();
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
            alert.setContentText("Please select order before edit!");
            alert.show();
        } else {
//            orderAddController.clearInput();
            orderAddController.updateMode();
            orderAddController.editOrder(selectedOrder);
            orderAddController.setCurrentOrder(selectedOrder);

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(addOrderView);
//            orderAddController.setRequestFocus();

        }

    }

    @FXML
    void deleteButtonOnClick() {
        hbox_addEditDelete.setVisible(false);
        hbox_confirmDelete.setVisible(true);

        tbc_checkbox.setVisible(true);
    }

    @FXML
    void backButtonOnClick() {
        hbox_addEditDelete.setVisible(true);
        hbox_confirmDelete.setVisible(false);

        tbc_checkbox.setVisible(false);

        uncheckAllCheckboxes();
        tableView.refresh();
    }

    private void uncheckAllCheckboxes() {
        for (Order order : tableView.getItems()) {
            order.setSelected(false);
        }
        selectedOrderIds.clear();
    }

    @FXML
    void deleteSelectedOrderOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + selectedOrderIds.size() + " customer? " +
                "If you delete, all order details belong to this order also get deleted.");
        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            selectedOrderIds.forEach(aLong -> {
                orderService.deleteOrder(aLong);
                Order order = orders.stream()
                        .filter(p -> p.getId() == aLong)
                        .findFirst()
                        .orElse(null);
                orders.remove(order);
            });

            showFirstPage();
            tableView.refresh();
        }
    }

    @FXML
    void deleteContextOnClick() {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer before deleting!");
            alert.show();
        } else {

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected customer? " +
                    "If you delete, all order details belong to that order also get deleted.");
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                orderService.deleteOrder(selectedOrder.getId());
                orders.remove(selectedOrder);
                tableView.getItems().remove(selectedOrder); // Remove the product from the TableView
                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();
            }
        }
    }

    private void searchOrderOnAction() {
        String searchText = txt_order_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Order> filter = orders.stream()
                    .filter(order -> order.getCustomer().getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            order.getEmployee().getName().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Order> filterCustomers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterCustomers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(orders);
            paginationHelper.showFirstPage();
        }
    }

    private void initEnterKeyPressing() {
        txt_order_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchOrderOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_order_search.clear();
                searchOrderOnAction();
            }
        });
    }

    @Override
    public void onOrderAdded(Order order) {
        orders.add(order);
        showLastPage();
    }
}
