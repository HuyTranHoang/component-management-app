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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.service.SupplierService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {

    // List
    private ObservableList<Supplier> suppliers;
    private Supplier currentSupplier;

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
    private PaginationHelper<Supplier> paginationHelper;

    //Service
    SupplierService supplierService = new SupplierService();


    //Validator
    @FXML
    private Label lbl_error_supplierWebsite;

    @FXML
    private Label lbl_error_supplierEmail;

    @FXML
    private Label lbl_error_supplierName;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private Label lbl_text;
    //Validator
    private final Validator validator = new Validator();
    private final ArrayList<Long> selectedSupplierIds = new ArrayList<>();
    private boolean isUpdate = false;

    // Customer Panel
    @FXML
    private TableView<Supplier> tableView;

    @FXML
    private TableColumn<Supplier, String> tbc_website;

    @FXML
    private TableColumn<Supplier, Boolean> tbc_checkbox;

    @FXML
    private TableColumn<Supplier, String> tbc_email;

    @FXML
    private TableColumn<Supplier, Long> tbc_id;

    @FXML
    private TableColumn<Supplier, String> tbc_name;


    @FXML
    private MFXTextField txt_website;

    @FXML
    private MFXTextField txt_email;

    @FXML
    private MFXTextField txt_name;


    @FXML
    private MFXTextField txt_supplier_search;

    @FXML
    private HBox hbox_addEditDelete;

    @FXML
    private HBox hbox_addGroup;

    @FXML
    private HBox hbox_confirmDelete;

    @FXML
    private HBox hbox_updateGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        suppliers = FXCollections.observableArrayList(supplierService.getAllSupplier());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(suppliers);
        paginationHelper.setTableView(tableView);

        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        paginationHelper.showFirstPage();

        initValidator();
        initEnterKeyPressing();

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
        tbc_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        tbc_website.setCellValueFactory(new PropertyValueFactory<>("website"));

        initCheckBox();
    }
    private void initCheckBox() {
        tbc_checkbox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Supplier, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Supplier supplier = getTableRow().getItem();
                    boolean selected = checkBox.isSelected();
                    supplier.setSelected(selected);
                    if (selected) {
                        selectedSupplierIds.add(supplier.getId());
                    } else {
                        selectedSupplierIds.remove(supplier.getId());
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
                TableRow<Supplier> currentRow = getTableRow();
                if (currentRow != null) {
                    currentRow.setStyle(selected ? "-fx-background-color: #ffb8b4;" : "");
                }
            }
        });
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
        txt_email.setOnKeyPressed(storeOrUpdateEventHandler);
        txt_website.setOnKeyPressed(storeOrUpdateEventHandler);

        txt_supplier_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchSupplierOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_supplier_search.clear();
                searchSupplierOnAction();
            }
        });
    }
    private void initValidator() {
        validator.createCheck()
                .dependsOn("name", txt_name.textProperty())
                .withMethod(context -> {
                    String name = context.get("name");
                    if (name.isEmpty())
                        context.error("Name can't be empty");
                    else if (name.length() > 255)
                        context.error("Name length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_supplierName);

        validator.createCheck()
                .dependsOn("email", txt_email.textProperty())
                .withMethod(context -> {
                    String suppliersEmail = context.get("email");
                    if (!suppliersEmail.matches("^(|([A-Za-z0-9._%+-]+@gmail\\.com))$")) {
                        context.error("Please enter a valid email address");
                    } else if (suppliersEmail.length() > 255) {
                        context.error("Email length exceeds the maximum limit of 255 characters");
                    } else if (isUpdate ? !isEmailUniqueUpdate(suppliers, suppliersEmail) : !isEmailUnique(suppliers, suppliersEmail)) {
                        context.error("This email is already in the database");
                    }

                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_supplierEmail);

        validator.createCheck()
                .dependsOn("website", txt_website.textProperty())
                .withMethod(context -> {
                    String description = context.get("website");
                    if (description.length() > 255)
                        context.error("Website length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_supplierWebsite);
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
    void storeButtonOnClick() {
        if (validator.validate()) {
            Supplier supplier = new Supplier();
            supplier.setName(txt_name.getText());
            supplier.setEmail(txt_email.getText());
            supplier.setWebsite(txt_website.getText());
            supplierService.addSupplier(supplier);
            suppliers.add(supplier);

            lbl_successMessage.setText("Add new supplier successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();
            showLastPage();
        }
    }
    @FXML
    void updateButtonOnClick() {
        if (validator.validate()) {
            currentSupplier.setName(txt_name.getText());
            currentSupplier.setEmail(txt_email.getText());
            currentSupplier.setWebsite(txt_website.getText());
            supplierService.updateSupplier(currentSupplier);

            lbl_successMessage.setText("Update supplier successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();
            int index = tableView.getItems().indexOf(currentSupplier);
            if (index >= 0) {
                tableView.getItems().set(index, currentSupplier);
            }
            addButtonOnClick();
        }
    }
    @FXML
    void clearButtonOnClick() {
        txt_name.clear();
        txt_email.clear();
        txt_website.clear();
        lbl_error_supplierName.setVisible(false);
        lbl_error_supplierEmail.setVisible(false);
        lbl_error_supplierWebsite.setVisible(false);
        txt_name.requestFocus();
    }
    @FXML
    void addButtonOnClick() {
        addMode();
    }
    @FXML
    void editButtonOnClick() {
        currentSupplier = tableView.getSelectionModel().getSelectedItem();
        if (currentSupplier == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select supplier before edit!");
            alert.show();
        } else {
            updateMode();
            txt_name.setText(currentSupplier.getName());
            if (currentSupplier.getEmail() != null)
                txt_email.setText(currentSupplier.getEmail());
            txt_website.setText(currentSupplier.getWebsite());
        }
    }

    @FXML
    void backButtonOnClick() {
        hbox_addEditDelete.setVisible(true);
        hbox_confirmDelete.setVisible(false);

        tbc_checkbox.setVisible(false);

        uncheckAllCheckboxes();
        tableView.refresh();
    }

    @FXML
    void deleteButtonOnClick() {
        hbox_addEditDelete.setVisible(false);
        hbox_confirmDelete.setVisible(true);

        tbc_checkbox.setVisible(true);
    }

    private void uncheckAllCheckboxes() {
        for (Supplier supplier : tableView.getItems()) {
            supplier.setSelected(false);
        }
        selectedSupplierIds.clear();
    }

    @FXML
    void deleteContextOnClick() {
        Supplier selectedSupplier= tableView.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a supplier before deleting!");
            alert.show();
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected supplier? " +
                    "If you delete, all products belong to that supplier also get deleted.");
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                supplierService.deleteSupplier(selectedSupplier.getId());
                suppliers.remove(selectedSupplier);
                tableView.getItems().remove(selectedSupplier); // Remove the product from the TableView
                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();
                addButtonOnClick();
            }
        }
    }

    @FXML
    void deleteSelectedSupplierOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + selectedSupplierIds.size() + " supplier? " +
                "If you delete, all products belong to this supplier also get deleted.");
        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            selectedSupplierIds.forEach(aLong -> {
                supplierService.deleteSupplier(aLong);
                Supplier supplier = suppliers.stream()
                        .filter(p -> p.getId() == aLong)
                        .findFirst()
                        .orElse(null);
                suppliers.remove(supplier);
            });

            addButtonOnClick();
            showFirstPage();
            tableView.refresh();
        }
    }
    private void searchSupplierOnAction() {
        String searchText = txt_supplier_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Supplier> filter = suppliers.stream()
                    .filter(supplier ->
                            supplier.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    supplier.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Supplier> filterSuppliers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterSuppliers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(suppliers);
            paginationHelper.showFirstPage();
        }
    }

    private void updateMode() {
        isUpdate = true;
        hbox_updateGroup.setVisible(true);
        hbox_addGroup.setVisible(false);
        lbl_text.setText("UPDATE SUPPLIER");
        clearButtonOnClick();
    }

    private void addMode() {
        isUpdate = false;
        hbox_updateGroup.setVisible(false);
        hbox_addGroup.setVisible(true);
        lbl_text.setText("ADD NEW SUPPLIER");
        clearButtonOnClick();
    }

    @FXML
    void resetFilterIconClicked() {
        txt_supplier_search.setText("");
        searchSupplierOnAction();
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

    private boolean isEmailUnique(List<Supplier> suppliers, String txt_email) {
        return suppliers.stream()
                .noneMatch(supplier -> supplier.getEmail() != null && supplier.getEmail().equals(txt_email));
    }

    private boolean isEmailUniqueUpdate(List<Supplier> suppliers, String txt_email) {
        String email = currentSupplier.getEmail();
        return suppliers.stream()
                .noneMatch(supplier -> supplier.getEmail() != null && supplier.getEmail().equals(txt_email))
                || txt_email.equals(email);
    }
}

