package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.service.ProductStorageService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class ProductImportExportController implements Initializable {

    @FXML
    private MFXComboBox<String> cbb_orderBy;

    @FXML
    private Button firstPageButton;

    @FXML
    private Button lastPageButton;

    @FXML
    private Label lbl_error_fromDate;

    @FXML
    private Label lbl_fromTo;

    @FXML
    private Label lbl_mostExportName;

    @FXML
    private Label lbl_mostExportQuantity;

    @FXML
    private Label lbl_mostImportName;

    @FXML
    private Label lbl_mostImportQuantity;


    @FXML
    private Button nextButton;

    @FXML
    private HBox pageButtonContainer;

    @FXML
    private Button previousButton;

    @FXML
    private TableView<ProductStorage> tableView;

    @FXML
    private TableColumn<ProductStorage, LocalDate> tbc_date;

    @FXML
    private TableColumn<ProductStorage, Integer> tbc_export;

    @FXML
    private TableColumn<ProductStorage, Integer> tbc_import;

    @FXML
    private TableColumn<ProductStorage, String> tbc_name;

    @FXML
    private TableColumn<ProductStorage, String> tbc_productCode;

    @FXML
    private MFXDatePicker txt_fromDate;

    @FXML
    private MFXDatePicker txt_toDate;
    private PaginationHelper<ProductStorage> paginationHelper;

    // Format
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Validator
    private Validator validator = new Validator();

    // Service
    private ProductService productService = new ProductService();
    private ProductStorageService productStorageService = new ProductStorageService();

    // List
    private ObservableList<ProductStorage> productStorages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        txt_fromDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_fromDate.getLocale()));
        txt_toDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_toDate.getLocale()));

        txt_fromDate.setValue(firstDayOfMonth);
        txt_toDate.setValue(lastDayOfMonth);

        paginationHelper = new PaginationHelper<>();
        initTableView();
        paginationHelper.setTableView(tableView);
        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        List<ProductStorage> productStorageList = productStorageService.getAllProductStorageFromTo(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        productStorages = FXCollections.observableArrayList(productStorageList);
        paginationHelper.setItems(productStorages);
        lbl_fromTo.setText("from " + dateTimeFormatter.format(firstDayOfMonth) + " to " + dateTimeFormatter.format(lastDayOfMonth));
        paginationHelper.showFirstPage();

        initComboBox();
        initValidator();
        initSort();
    }

    private void initValidator() {
        validator.createCheck()
                .dependsOn("fromDate", txt_fromDate.valueProperty())
                .dependsOn("toDate",txt_toDate.valueProperty())
                .withMethod(context -> {
                    LocalDate fromDate = context.get("fromDate");
                    LocalDate toDate =  context.get("toDate");
                    if (fromDate.isAfter(toDate))
                        context.error("From date can't be after to date");
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_fromDate);
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

    private void initTableView() {
        tbc_productCode.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            if (product != null) {
                return new ReadOnlyObjectWrapper<>(product.getProductCode());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tbc_name.setCellValueFactory(cellData -> {
            Product product = cellData.getValue().getProduct();
            if (product != null) {
                return new ReadOnlyObjectWrapper<>(product.getName());
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        tbc_import.setCellValueFactory(new PropertyValueFactory<>("importQuantity"));
        tbc_export.setCellValueFactory(new PropertyValueFactory<>("exportQuantity"));

        // Định nghĩa StringConverter để chuyển đổi LocalDate thành chuỗi "dd/MM/yyyy"
        StringConverter<LocalDate> dateToStringConverter = new LocalDateStringConverter();
        tbc_date.setCellValueFactory(new PropertyValueFactory<>("dateOfStorage"));
        tbc_date.setCellFactory(TextFieldTableCell.forTableColumn(dateToStringConverter));

    }

    private void initComboBox() {
        List<String> orderList = List.of("ASC", "DESC");
        cbb_orderBy.setItems(FXCollections.observableArrayList(orderList));
        cbb_orderBy.selectLast();
    }

    private void initSort() {
        cbb_orderBy.setItems(FXCollections.observableArrayList(List.of("ASC", "DESC")));
        // Add listeners to both ComboBoxes
        cbb_orderBy.valueProperty().addListener((observable, oldValue, newValue) -> applySorting());
    }

    private void applySorting() {
        String orderBy = cbb_orderBy.getValue();
        Comparator<ProductStorage> comparator = Comparator.comparing(ProductStorage::getDateOfStorage);
        if ("DESC".equals(orderBy)) {
            comparator = comparator.reversed();
        }

        FXCollections.sort(productStorages, comparator);
        showFirstPage();
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
    void showNextPage() {
        paginationHelper.showNextPage();
    }

    @FXML
    void showPreviousPage() {
        paginationHelper.showPreviousPage();
    }

    @FXML
    public void viewButtonOnClick() {
        if(validator.validate()){
            LocalDate fromDate = txt_fromDate.getValue();
            LocalDate toDate = txt_toDate.getValue();

            productStorages.clear();

            List<ProductStorage> productStorageList = productStorageService.getAllProductStorageFromTo(fromDate, toDate.plusDays(1));
            productStorages = FXCollections.observableArrayList(productStorageList);
            paginationHelper.setItems(productStorages);
            applySorting();
            paginationHelper.showFirstPage();

            lbl_fromTo.setText("from " + dateTimeFormatter.format(fromDate) + " to " + dateTimeFormatter.format(toDate));


            // Nhóm các ProductStorage theo productId và tính tổng importQuantity và exportQuantity cho mỗi nhóm
            Map<Long, Integer> importQuantitiesByProductId = productStorageList.stream()
                    .collect(Collectors.groupingBy(ProductStorage::getProductId, Collectors.summingInt(ProductStorage::getImportQuantity)));

            Map<Long, Integer> exportQuantitiesByProductId = productStorageList.stream()
                    .collect(Collectors.groupingBy(ProductStorage::getProductId, Collectors.summingInt(ProductStorage::getExportQuantity)));

            // Lấy ra ID với importQuantity cao nhất
            Long idWithHighestImport = importQuantitiesByProductId.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // Lấy ra ID với exportQuantity cao nhất
            Long idWithHighestExport = exportQuantitiesByProductId.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // Lấy tổng importQuantity cao nhất
            int highestTotalImportQuantity = importQuantitiesByProductId.values().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            // Lấy tổng exportQuantity cao nhất
            int highestTotalExportQuantity = exportQuantitiesByProductId.values().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            // Check if idWithHighestExport is not null before using it
            if (idWithHighestExport != null && idWithHighestImport != null) {
                Product hightImport = productService.getProductById(idWithHighestImport);
                lbl_mostImportName.setText("> " + hightImport.getName());
                lbl_mostImportQuantity.setText(String.valueOf(highestTotalImportQuantity));

                Product hightExport = productService.getProductById(idWithHighestExport);
                lbl_mostExportName.setText("> " + hightExport.getName());
                lbl_mostExportQuantity.setText(String.valueOf(highestTotalExportQuantity));
            } else {
                lbl_mostImportName.setText("...");
                lbl_mostExportName.setText("...");
            }

        }
    }



}
