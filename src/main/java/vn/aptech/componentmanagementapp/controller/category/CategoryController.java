package vn.aptech.componentmanagementapp.controller.category;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
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
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.service.CategoryService;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    //List
    private ObservableList<Category> categories;

    private Category currentCategory;

    //Service
    private final CategoryService categoryService = new CategoryService();

    //Validator
    private final Validator validator = new Validator();

    private final ArrayList<Long> selectedCategoryIds = new ArrayList<>();

    @FXML
    private Button firstPageButton;

    @FXML
    private HBox hbox_addEditDelete;

    @FXML
    private HBox hbox_addGroup;

    @FXML
    private HBox hbox_confirmDelete;

    @FXML
    private HBox hbox_updateGroup;

    @FXML
    private Button lastPageButton;

    @FXML
    private Label lbl_error_categoryDescription;

    @FXML
    private Label lbl_error_categoryName;

    @FXML
    private Label lbl_modeTitle;

    @FXML
    private Label lbl_successMessage;

    @FXML
    private Button nextButton;

    @FXML
    private HBox pageButtonContainer;

    @FXML
    private Button previousButton;

    @FXML
    private TableView<Category> tableView;

    @FXML
    private TableColumn<Category, Boolean> tbc_checkbox;

    @FXML
    private TableColumn<Category, Long> tbc_id;

    @FXML
    private TableColumn<Category, String> tbc_name;

    @FXML
    private TableColumn<Category, String> tbc_description;

    @FXML
    private MFXTextField txt_category_search;

    @FXML
    private TextArea txt_description;

    @FXML
    private MFXTextField txt_name;

    private PaginationHelper<Category> paginationHelper;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categories = FXCollections.observableArrayList(categoryService.getAllCategory());
        initTableView();

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setItems(categories);
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
        tbc_description.setCellValueFactory(new PropertyValueFactory<>("description"));

        initCheckBox();
    }

    private void initCheckBox() {
        tbc_checkbox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbc_checkbox.setCellFactory(column -> new CheckBoxTableCell<Category, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Category category = getTableRow().getItem();
                    boolean selected = checkBox.isSelected();
                    category.setSelected(selected);
                    if (selected) {
                        selectedCategoryIds.add(category.getId());
                    } else {
                        selectedCategoryIds.remove(category.getId());
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
                TableRow<Category> currentRow = getTableRow();
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
//        txt_description.setOnKeyPressed(storeOrUpdateEventHandler);

        txt_category_search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchCategoryOnAction();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                txt_category_search.clear();
                searchCategoryOnAction();
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
                    else if (name.matches("\\d+"))
                        context.error("Name can't contain digits");
                    else if (name.length() > 255)
                        context.error("Name length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_categoryName);

        validator.createCheck()
                .dependsOn("description", txt_description.textProperty())
                .withMethod(context -> {
                    String description = context.get("description");
                    if (description.length() > 255)
                        context.error("Description length exceeds the maximum limit of 255 characters");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_categoryDescription);
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

    @FXML
    void storeButtonOnClick() {
        if (validator.validate()) {
            Category category = new Category();
            category.setName(txt_name.getText());
            category.setDescription(txt_description.getText());
            categoryService.addCategory(category);
            categories.add(category);

            clearButtonOnClick();

            lbl_successMessage.setText("Add new category successfully!!");
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
            currentCategory.setName(txt_name.getText());
            currentCategory.setDescription(txt_description.getText());
            categoryService.updateCategory(currentCategory);

            lbl_successMessage.setText("Update category successfully!!");
            lbl_successMessage.setVisible(true);
            new FadeIn(lbl_successMessage).play();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> new FadeOut(lbl_successMessage).play()));
            timeline.play();
            int index = tableView.getItems().indexOf(currentCategory);
            if (index >= 0) {
                tableView.getItems().set(index, currentCategory);
            }
            addButtonOnClick();
        }
    }

    @FXML
    void clearButtonOnClick() {
        txt_name.clear();
        txt_description.clear();
    }

    @FXML
    void editButtonOnClick() {
        currentCategory = tableView.getSelectionModel().getSelectedItem();
        if (currentCategory == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select category before edit!");
            alert.show();
        } else {
            updateMode();
            txt_name.setText(currentCategory.getName());
            if (currentCategory.getDescription() != null)
                txt_description.setText(currentCategory.getDescription());
        }
    }

    @FXML
    void addButtonOnClick() {
        addMode();
    }

    @FXML
    void deleteButtonOnClick() {
        hbox_addEditDelete.setVisible(false);
        hbox_confirmDelete.setVisible(true);

        tbc_checkbox.setVisible(true);
    }

    private void uncheckAllCheckboxes() {
        for (Category category : tableView.getItems()) {
            category.setSelected(false);
        }
        selectedCategoryIds.clear();
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
    void deleteSelectedCategoryOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + selectedCategoryIds.size() + " category? " +
                "If you delete, all products belong to this category also get deleted.");
        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            selectedCategoryIds.forEach(aLong -> {
                categoryService.deleteCategory(aLong);
                Category category = categories.stream()
                        .filter(p -> p.getId() == aLong)
                        .findFirst()
                        .orElse(null);
                categories.remove(category);
            });

            addButtonOnClick();
            showFirstPage();
            tableView.refresh();
        }
    }

    @FXML
    void deleteContextOnClick() {
        Category selectedCategory = tableView.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a category before deleting!");
            alert.show();
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete selected category? " +
                    "If you delete, all products belong to that category also get deleted.");
            if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
                categoryService.deleteCategory(selectedCategory.getId());
                categories.remove(selectedCategory);
                tableView.getItems().remove(selectedCategory); // Remove the product from the TableView
                if (paginationHelper.getPageItems().isEmpty())
                    showPreviousPage();
                addButtonOnClick();
            }
        }
    }

    private void updateMode() {
        hbox_updateGroup.setVisible(true);
        hbox_addGroup.setVisible(false);
        lbl_modeTitle.setText("UPDATE CATEGORY");
        clearButtonOnClick();
    }

    private void addMode() {
        hbox_updateGroup.setVisible(false);
        hbox_addGroup.setVisible(true);
        lbl_modeTitle.setText("ADD NEW CATEGORY");
        clearButtonOnClick();
    }

    private void searchCategoryOnAction() {
        String searchText = txt_category_search.getText().trim();
        if (!searchText.isEmpty()) {
            List<Category> filter = categories.stream()
                    .filter(category ->
                            category.getName().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            ObservableList<Category> filterCustomers = FXCollections.observableArrayList(filter);

            paginationHelper.setItems(filterCustomers);
            paginationHelper.showFirstPage();
        } else {
            paginationHelper.setItems(categories);
            paginationHelper.showFirstPage();
        }
    }

    @FXML
    void resetFilterIconClicked() {
        txt_category_search.setText("");
        searchCategoryOnAction();
    }

}
