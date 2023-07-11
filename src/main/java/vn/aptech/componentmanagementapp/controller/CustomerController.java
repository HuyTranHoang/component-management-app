package vn.aptech.componentmanagementapp.controller;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.service.CustomerService;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
//    List
    private ObservableList<Customer> customers;
    private ObservableList<Customer> pageItems;

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
    @FXML
    private HBox paginationControls;
    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;

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
    private Label lbl_successMessage;

    Validator customerValidator = new Validator();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customers = FXCollections.observableArrayList(customerService.getAllCustomer());
        initTableView();
        showPage(currentPageIndex);
        updatePageButtons();

        initValidator();
    }

    private void initTableView() {
        tbc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbc_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        tbc_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tbc_email.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    @FXML
    void clearButton(){
        txt_name.setText("");
        txt_address.setText("");
        txt_phone.setText("");
        txt_email.setText("");
        txt_name.requestFocus();
    }
    @FXML
    void storeButton(){
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
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                new FadeOut(lbl_successMessage).play();
            }));
            timeline.play();
            showLastPage();
            updatePageButtons();
        }
    }

    private void initValidator() {
        customerValidator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String customerName = context.get("name");
                    if (customerName.isEmpty())
                        context.error("Name can't be empty");
                    else if(!customerName.matches("\\D+"))
                        context.error("Name can't have number");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerName);

        customerValidator.createCheck()
                .dependsOn("address", txt_address.textProperty())
                .withMethod(context -> {
                    String customerAddress = context.get("address");
                    if (customerAddress.isEmpty())
                        context.error("Address can't be empty");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerAddress);
        customerValidator.createCheck()
                .dependsOn("phone", txt_phone.textProperty())
                .withMethod(context -> {
                    String customerPhone = context.get("phone");
                    if (customerPhone.isEmpty())
                        context.error("Phone can't be empty");
                    else if(!customerPhone.matches("\\d+"))
                        context.error("Phone can only contain number");
                    else if(!customerPhone.matches("^.{1,10}$"))
                        context.error("Phone maximum limit is 10 numbers");
                    else if(!customerPhone.matches("^\\d{10}$"))
                        context.error("Phone requirements must be 10 digits");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_customerPhone);

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
    /*
     * Begin of Pagination
     */

    private void showPage(int pageIndex) {
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, customers.size());

        pageItems = FXCollections.observableArrayList(customers.subList(startIndex, endIndex));
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
        int maxPageIndex = (int) Math.ceil((double) customers.size() / ITEMS_PER_PAGE) - 1;
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
        int maxPageIndex = (int) Math.ceil((double) customers.size() / ITEMS_PER_PAGE) - 1;
        currentPageIndex = maxPageIndex;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageCount = (int) Math.ceil((double) customers.size() / ITEMS_PER_PAGE);
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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) customers.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    /*
     * End of pagination
     */



}
