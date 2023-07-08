package vn.aptech.componentmanagementapp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductController implements Initializable {


    @FXML
    private HBox pageButtonContainer;

//    Pagination
    private static final int ITEMS_PER_PAGE = 8;
    private static final int COLUMNS = 4;
    private static final ObservableList<String> items = FXCollections.observableArrayList();
    private int currentPageIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 1; i <= 80; i++) {
            items.add("Item " + i);
        }
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void showPage(int pageIndex) {
//        product_gridPanel.getChildren().clear();

        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        int currentRow = 0;
        int currentColumn = 0;

        for (int i = startIndex; i < endIndex; i++) {
            String item = items.get(i);

            Button itemButton = new Button(item);
            itemButton.setMinWidth(80);
            itemButton.setOnAction(e -> System.out.println(item)); // Example action

//            product_gridPanel.add(itemButton, currentColumn, currentRow);

            currentColumn++;

            if (currentColumn == COLUMNS) {
                currentColumn = 0;
                currentRow++;
            }
        }
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
    private void showNextPage() {
        int maxPageIndex = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1;
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
        currentPageIndex = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    private void updatePageButtons() {
        int pageCount = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }
}
