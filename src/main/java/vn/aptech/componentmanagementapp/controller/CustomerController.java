package vn.aptech.componentmanagementapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.service.CustomerService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
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
    private TableColumn<Customer, String> tbc_address;

    @FXML
    private TableColumn<Customer, String> tbc_email;

    @FXML
    private TableColumn<Customer, Long> tbc_id;

    @FXML
    private TableColumn<Customer, String> tbc_name;

    @FXML
    private TableColumn<Customer, String> tbc_phone;

    @FXML
    private MFXTextField txt_address;

    @FXML
    private MFXTextField txt_email;

    @FXML
    private MFXTextField txt_name;

    @FXML
    private MFXTextField txt_phone;

    @FXML
    private MFXTextField txt_product_search;

    @FXML
    private HBox hbox_addGroup;
    @FXML
    private HBox hbox_updateGroup;
    @FXML
    private Label lbl_text;
    //Service
    CustomerService customerService = new CustomerService();

    //Validate
    @FXML
    private Label lbl_error_customerAddress;

    @FXML
    private Label lbl_error_customerName;

    @FXML
    private Label lbl_error_customerPhone;

    @FXML
    private Label lbl_error_customerEmail;

    @FXML
    private Label lbl_successMessage;

    Validator customerValidator = new Validator();
    private boolean isUpdate = false;

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

        //Validate
        initValidator();

        initEnterKeyPressing();

        // Double click thì edit
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem() != null) {
                        editButtonOnClick();
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
    void storeButtonOnClick(){
        if(customerValidator.validate()){
            Customer customer = new Customer();
            customer.setName(txt_name.getText());
            customer.setAddress(txt_address.getText());
            customer.setPhone(txt_phone.getText());
            customer.setEmail(txt_email.getText());
            customerService.addCustomer(customer);
            customers.add(customer);


            // Show success message
            lbl_successMessage.setText("Add new customer successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();
            showLastPage();
        }
    }
    @FXML
    void updateButtonOnClick(){
        if(customerValidator.validate()){
            Customer customer = tableView.getSelectionModel().getSelectedItem();
            customer.setName(txt_name.getText());
            customer.setAddress(txt_address.getText());
            customer.setPhone(txt_phone.getText());
            customer.setEmail(txt_email.getText());
            customerService.updateCustomer(customer);

            // Show success message
            lbl_successMessage.setText("Update customer successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            // Hide the message after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();

            int index = tableView.getItems().indexOf(customer);
            if (index >= 0) {
                tableView.getItems().set(index, customer);
            }

            showLastPage();
        }
    }
    @FXML
    void deleteButtonOnClick() {
        Customer selectedCustomer = tableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer before deleting!");
            alert.show();
        } else {

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected customer?");
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                customerService.deleteCustomer((int) selectedCustomer.getId());
                customers.remove(selectedCustomer);
                tableView.getItems().remove(selectedCustomer); // Remove the product from the TableView
                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();
            }
        }
    }
    @FXML
    void editButtonOnClick() {
        updateMode();
        Customer customer = tableView.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select customer before edit!");
            alert.show();
        } else {
            txt_name.setText(customer.getName());
            txt_address.setText(customer.getAddress());
            txt_phone.setText(customer.getPhone());
            if (customer.getEmail() != null)
                txt_email.setText(customer.getEmail());
        }
    }
    @FXML
    void updateMode() {
        isUpdate = true;
        hbox_updateGroup.setVisible(true);
        hbox_addGroup.setVisible(false);
        lbl_text.setText("UPDATE CUSTOMER");
        clearButtonOnClick();

    }
    @FXML
    void addMode() {
        isUpdate = false;
        hbox_updateGroup.setVisible(false);
        hbox_addGroup.setVisible(true);
        lbl_text.setText("ADD NEW CUSTOMER");
        clearButtonOnClick();
    }
    @FXML
    void clearButtonOnClick(){
        txt_name.setText("");
        txt_address.setText("");
        txt_phone.setText("");
        txt_email.setText("");
        lbl_error_customerName.setVisible(false);
        lbl_error_customerAddress.setVisible(false);
        lbl_error_customerPhone.setVisible(false);
        lbl_error_customerEmail.setVisible(false);
        txt_name.requestFocus();
    }
    private void initValidator() {
        customerValidator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String customerName = context.get("name");
                    if (customerName.isEmpty()) {
                        context.error("Name can't be empty");
                    } else if (!customerName.matches("\\D+")) {
                        context.error("Name can't have numbers");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerName);

        customerValidator.createCheck()
                .dependsOn("address", txt_address.textProperty())
                .withMethod(context -> {
                    String customerAddress = context.get("address");
                    if (customerAddress.isEmpty()) {
                        context.error("Address can't be empty");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerAddress);

        customerValidator.createCheck()
                .dependsOn("email", txt_email.textProperty())
                .withMethod(context -> {
                    String customerEmail = context.get("email");
                    if (!customerEmail.matches("^(|([A-Za-z0-9._%+-]+@gmail\\.com))$")) {
                        context.error("Please enter a valid email address");
                    } else if (isUpdate ? !isEmailUniqueUpdate(customers, customerEmail) : !isEmailUnique(customers, customerEmail)) {
                        context.error("This email is already in the database");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerEmail);

        customerValidator.createCheck()
                .dependsOn("phone", txt_phone.textProperty())
                .withMethod(context -> {
                    String customerPhone = context.get("phone");
                    if (customerPhone.isEmpty()) {
                        context.error("Phone can't be empty");
                    } else if (!customerPhone.matches("\\d+")) {
                        context.error("Phone can only contain numbers");
                    } else if (!customerPhone.matches("^.{1,10}$")) {
                        context.error("Phone maximum limit is 10 numbers");
                    } else if (!customerPhone.matches("^\\d{10}$")) {
                        context.error("Phone must have 10 digits");
                    } else if (isUpdate ? !isPhoneUniqueUpdate(customers, customerPhone) : !isPhoneUnique(customers, customerPhone)) {
                        context.error("This phone is already in the database");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerPhone);
    }

    private boolean isEmailUnique(List<Customer> customers, String txt_email) {
        return customers.stream()
                .noneMatch(customer -> customer.getEmail() != null && customer.getEmail().equals(txt_email));
    }

    private boolean isEmailUniqueUpdate(List<Customer> customers, String txt_email) {
        String email = tableView.getSelectionModel().getSelectedItem().getEmail();
        return customers.stream()
                .noneMatch(customer -> customer.getEmail() != null && customer.getEmail().equals(txt_email))
                || txt_email.equals(email);
    }

    private boolean isPhoneUnique(List<Customer> customers, String txt_phone) {
        return customers.stream()
                .noneMatch(customer -> customer.getPhone() != null && customer.getPhone().equals(txt_phone));
    }

    private boolean isPhoneUniqueUpdate(List<Customer> customers, String txt_phone) {
        String phone = tableView.getSelectionModel().getSelectedItem().getPhone();
        return customers.stream()
                .noneMatch(customer -> customer.getPhone() != null && customer.getPhone().equals(txt_phone))
                || txt_phone.equals(phone);
    }

    private Decoration labelDecorator(ValidationMessage message) {
        return new Decoration() {
            @Override
            public void add(Node target) {
                ((Label) target).setText(message.getText());
                target.setVisible(true);
            }

            @Override
            public void remove(Node target) {
                target.setVisible(false);
            }
        };
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

    private void initEnterKeyPressing() {
        EventHandler<KeyEvent> storeOrUpdateEventHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (hbox_addGroup.visibleProperty().get()) {
                    storeButtonOnClick();
                } else {
                    updateButtonOnClick();
                }
            }
        };

        txt_name.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_address.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_phone.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_email.setOnKeyPressed(storeOrUpdateEventHandler);
    }



}
