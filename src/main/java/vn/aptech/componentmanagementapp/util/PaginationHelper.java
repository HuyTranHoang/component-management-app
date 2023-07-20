package vn.aptech.componentmanagementapp.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class PaginationHelper<T> {
    private static final int ITEMS_PER_PAGE = 26;
    private int currentPageIndex = 0;

    private ObservableList<T> items;
    private ObservableList<T> pageItems;
    private TableView<T> tableView;
    private Button firstPageButton;
    private Button lastPageButton;
    private Button nextButton;
    private HBox pageButtonContainer;
    private Button previousButton;

    public ObservableList<T> getPageItems() {
        return pageItems;
    }

    public void setFirstPageButton(Button firstPageButton) {
        this.firstPageButton = firstPageButton;
    }

    public void setLastPageButton(Button lastPageButton) {
        this.lastPageButton = lastPageButton;
    }

    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
    }

    public void setPageButtonContainer(HBox pageButtonContainer) {
        this.pageButtonContainer = pageButtonContainer;
    }

    public void setPreviousButton(Button previousButton) {
        this.previousButton = previousButton;
    }

    public void setItems(ObservableList<T> items) {
        this.items = items;
    }

    public void setTableView(TableView<T> tableView) {
        this.tableView = tableView;
    }

    public void showPage(int pageIndex) {
        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        pageItems = FXCollections.observableArrayList(items.subList(startIndex, endIndex));
        tableView.setItems(pageItems);
    }

    public void showCurrentPage() {
        if (pageItems != null && pageItems.isEmpty()) {
            showPreviousPage();
        } else {
            showPage(currentPageIndex);
        }
    }

    public void showPreviousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    public void showNextPage() {
        int maxPageIndex = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1;
        if (currentPageIndex < maxPageIndex) {
            currentPageIndex++;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

    public void showFirstPage() {
        currentPageIndex = 0;
        showPage(currentPageIndex);
        updatePageButtons();
    }

    public void showLastPage() {
        int maxPageIndex = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1;
        currentPageIndex = maxPageIndex;
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
        if (pageIndex >= 0 && pageIndex <= (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE) - 1) {
            currentPageIndex = pageIndex;
            showPage(currentPageIndex);
            updatePageButtons();
        }
    }

}
