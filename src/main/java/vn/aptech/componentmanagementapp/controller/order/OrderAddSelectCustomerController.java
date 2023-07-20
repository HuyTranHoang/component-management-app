package vn.aptech.componentmanagementapp.controller.order;

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
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.service.CustomerService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderAddSelectCustomerController implements Initializable {
    public interface CustomerSelectionCallback {
        void onCustomerSelected(Customer customer);
    }
    private CustomerSelectionCallback customerSelectionCallback;

    public void setCustomerSelectionCallback(CustomerSelectionCallback callback) {
        this.customerSelectionCallback = callback;
    }

    //    List
    private ObservableList<Customer> customers;
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
    private PaginationHelper<Customer> paginationHelper;

    // Customer Panel
    @FXML
    private TableView<Customer> tableView;
    @FXML
    private TableColumn<Customer, Long> tbc_id;
    @FXML
    private TableColumn<Customer, String> tbc_name;
    @FXML
    private TableColumn<Customer, String> tbc_address;
    @FXML
    private TableColumn<Customer, String> tbc_phone;
    @FXML
    private TableColumn<Customer, String> tbc_email;

    @FXML
    private MFXTextField txt_customer_search;
    //Service
    CustomerService customerService = new CustomerService();

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customers = FXCollections.observableArrayList(customerService.getAllCustomer());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(customers);
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
                Customer customer = tableView.getSelectionModel().getSelectedItem();
                if (customerSelectionCallback != null) {
                    customerSelectionCallback.onCustomerSelected(customer);
                    stage.close();
                }
            }
        });

    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tbc_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tbc_email.setCellValueFactory(new PropertyValueFactory<>("email"));

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


    private void searchCustomerOnAction() {
        String searchText = txt_customer_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Customer> filter = customers.stream()
                    .filter(customer ->
                            customer.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    customer.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                                    customer.getPhone().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Customer> filterCustomers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterCustomers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(customers);
            paginationHelper.showFirstPage();
        }
    }

    private void initEnterKeyPressing() {
        txt_customer_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchCustomerOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_customer_search.clear();
                searchCustomerOnAction();
            }
        });
    }

    @FXML
    void resetFilterIconClicked() {
        txt_customer_search.setText("");
        searchCustomerOnAction();
    }
}
