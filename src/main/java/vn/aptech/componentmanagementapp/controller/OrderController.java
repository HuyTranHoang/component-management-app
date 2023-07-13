package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import vn.aptech.componentmanagementapp.model.Order;

public class OrderController {

    @FXML
    private MFXButton btn_deleteCheckedOrder;

    @FXML
    private MFXComboBox<?> cbb_orderBy;

    @FXML
    private MFXComboBox<?> cbb_sortBy;

    @FXML
    private Label filter_noti_label;

    @FXML
    private Circle filter_noti_shape;

    @FXML
    private Button firstPageButton;

    @FXML
    private Button lastPageButton;

    @FXML
    private Button nextButton;

    @FXML
    private AnchorPane orderView;

    @FXML
    private HBox pageButtonContainer;

    @FXML
    private Button previousButton;

    @FXML
    private TableView<Order> tableView;

    @FXML
    private TableColumn<?, ?> tbc_customerId;

    @FXML
    private TableColumn<?, ?> tbc_deliveryDate;

    @FXML
    private TableColumn<?, ?> tbc_deliveryLocation;

    @FXML
    private TableColumn<?, ?> tbc_employeeId;

    @FXML
    private TableColumn<?, ?> tbc_id;

    @FXML
    private TableColumn<?, ?> tbc_note;

    @FXML
    private TableColumn<?, ?> tbc_orderDate;

    @FXML
    private TableColumn<?, ?> tbc_shipmentDate;

    @FXML
    private MFXTextField txt_order_search;

    //    List
    private ObservableList<Order> orders;
    private ObservableList<Order> pageItems;


    //  Pagination

    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;

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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) orders.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    /*
     * End of pagination
     */
}
