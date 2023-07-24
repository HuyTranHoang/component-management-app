package vn.aptech.componentmanagementapp.controller.order;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.service.EmployeeService;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable, OrderAddController.OrderAddCallback,
        OrderFilterController.ViewResultCallback, OrderFilterController.ClearFilterCallback {
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
    private TableColumn<Order, LocalDateTime> tbc_receiveDate;
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

    //    Filter Panel
    private Scene filterScene;
    private Stage filterStage;

    @FXML
    private Label filter_noti_label; // Truyền vào OrderFilterController để set visiable và text
    @FXML
    private Circle filter_noti_shape;


    //    Controller to call clear filter function in this
    private OrderFilterController filterController;
    private OrderAddController orderAddController;
    private OrderShowController orderShowController;

    //  Service
    private final OrderService orderService = new OrderService();
    private final EmployeeService employeeService = new EmployeeService();

    // Cached views
    private AnchorPane addOrderView;
    private AnchorPane showOrderView;

    @FXML
    private AnchorPane orderView;
    private AnchorPane anchor_main_rightPanel; // Truyền từ Main controller vào

    private Employee currentEmployee;
    private Employee loginEmployee;

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

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

        initFilterStage();
        filterController.initSearchListen();
        initTableViewEvent();
        initSort();

        Platform.runLater(() -> {
            loginEmployee = employeeService.getEmployeeById(currentEmployee.getId());
        });
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tbc_deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        tbc_receiveDate.setCellValueFactory(new PropertyValueFactory<>("receiveDate"));
        tbc_deliveryLocation.setCellValueFactory(new PropertyValueFactory<>("deliveryLocation"));
//        tbc_totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        tbc_totalAmount.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_totalAmount.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getTotalAmount()));
        tbc_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        tbc_customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tbc_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

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
        tbc_receiveDate.setCellFactory(formatCellFactory(formatter));

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

    private void initTableViewEvent() {
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                showButtonOnClick();
            }
        });
    }

    private void initSort() {
        cbb_sortBy.setItems(FXCollections.observableArrayList(
                List.of("Id", "Order date", "Delivery date", "Receive date", "Total amount", "Customer name", "Employee name")));
        cbb_orderBy.setItems(FXCollections.observableArrayList(List.of("ASC", "DESC")));
        // Add listeners to both ComboBoxes
        cbb_sortBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
        cbb_orderBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
    }

    private void applySorting() {
        String sortBy = cbb_sortBy.getValue();
        String orderBy = cbb_orderBy.getValue();
        Comparator<Order> comparator = switch (sortBy) {
            case "Order date" -> Comparator.comparing(Order::getOrderDate);
            case "Delivery date" -> Comparator.comparing(Order::getDeliveryDate);
            case "Receive date" -> Comparator.comparing(Order::getReceiveDate);
            case "Total amount" -> Comparator.comparing(Order::getTotalAmount);
            case "Customer name" -> Comparator.comparing(order -> order.getCustomer().getName());
            case "Employee name" -> Comparator.comparing(order -> order.getEmployee().getName());
            default -> Comparator.comparing(Order::getId);
        };
        // Check the selected value of cbb_orderBy and adjust the comparator accordingly
        if ("DESC".equals(orderBy)) {
            comparator = comparator.reversed();
        }
        // Sort the products list with the chosen comparator
        FXCollections.sort(orders, comparator);
        showFirstPage();
    }

    private void initFilterStage() {
        try {
            if (filterScene == null && filterStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/order-filter.fxml"));
                filterScene = new Scene(fxmlLoader.load());
                filterStage = new Stage();
                filterStage.setTitle("Filter Order");
                filterStage.initModality(Modality.APPLICATION_MODAL);

                filterController = fxmlLoader.getController();
                filterController.setOrders(orders);

                // Xử lý dữ liệu sau khi viewResultButtonOnClick() được gọi và nhận filterOrder
                // ... thực hiện các thao tác khác với filterOrder ...
//                filterController.setViewResultCallback(filterOrder -> {
//                    paginationHelper.setItems(FXCollections.observableArrayList(filterOrder));
//                    paginationHelper.showFirstPage();
//                });

                Image image = null;

                URL resourceURL = ComponentManagementApplication.class.getResource("images/order.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }

                filterStage.getIcons().add(image);

                filterStage.initModality(Modality.APPLICATION_MODAL);

                filterController.setStage(filterStage);
                filterController.setViewResultCallback(this);
                filterController.setClearFilterCallback(this);
                filterController.setFilter_noti_label(filter_noti_label);
                filterController.setFilter_noti_shape(filter_noti_shape);
                filterController.setTxt_order_search(txt_order_search);

                filterStage.setScene(filterScene);
                filterStage.setResizable(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void filterButtonOnClick() {
        filterStage.show();
    }

    @FXML
    public void resetFilterIconClicked() {
        if (filterController != null) {
            filterController.clearFilterButtonOnClick();
        }
        cbb_sortBy.selectFirst();
        cbb_orderBy.selectFirst();

        orders = FXCollections.observableArrayList(orderService.getAllOrder());
        paginationHelper.setItems(orders);
        showFirstPage();

        uncheckAllCheckboxes();
    }

    @FXML
    void addButtonOnClick() {
        if (addOrderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/order-add.fxml"));
                addOrderView = fxmlLoader.load();
                orderAddController = fxmlLoader.getController();
                orderAddController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderAddController.setOrderView(orderView);
                orderAddController.setTableView(tableView);
                orderAddController.setCurrentEmployee(currentEmployee);

                orderAddController.setOrderAddCallback(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        orderAddController.clearInputOrder();

        Employee employee = employeeService.getEmployeeById(loginEmployee.getId());
        orderAddController.setCurrentEmployee(employee);
        orderAddController.setTextEmployeeName(employee);

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(addOrderView);
//        productAddController.setRequestFocus();
    }


    @FXML
    void showButtonOnClick() {
        if (showOrderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/order-show.fxml"));
                showOrderView = fxmlLoader.load();
                orderShowController = fxmlLoader.getController();
                orderShowController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderShowController.setOrderView(orderView);
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
            orderShowController.setCurrentOrder(selectedOrder);
            orderShowController.setInformation();

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(showOrderView);

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
        confirmation.setContentText("Are you sure you want to delete " + selectedOrderIds.size() + " order? " +
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
            confirmation.setContentText("Are you sure you want to delete selected order? " +
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

    @Override
    public void onOrderAdded(Order order) {
        orders.add(order);
        resetFilterIconClicked();
        showLastPage();
    }

    @Override
    public void onViewResultClicked(List<Order> filterOrder) {
        paginationHelper.setItems(FXCollections.observableArrayList(filterOrder));
        showFirstPage();
    }

    @Override
    public void onClearFilterClicked() {
        orders = FXCollections.observableArrayList(orderService.getAllOrder());
        paginationHelper.setItems(orders);
        showFirstPage();

        uncheckAllCheckboxes();
    }

    public void reloadOrder() {
        orders = FXCollections.observableArrayList(orderService.getAllOrder());
        paginationHelper.setItems(orders);
        showFirstPage();
    }
}
