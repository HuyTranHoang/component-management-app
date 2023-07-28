package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ProductRevenueController implements Initializable {

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
    private MFXDatePicker txt_fromDate;

    @FXML
    private MFXDatePicker txt_toDate;

    @FXML
    private Label lbl_totalProduct;

    @FXML
    private Label lbl_totalProductOutOfStock;

    @FXML
    private MFXComboBox<String> cbb_belowQuantity;

    @FXML
    private MFXComboBox<String> cbb_orderBy;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, String> tbc_name;

    @FXML
    private TableColumn<Product, Double> tbc_price;

    @FXML
    private TableColumn<Product, String> tbc_productCode;

    @FXML
    private TableColumn<Product, Double> tbc_revenue;

    @FXML
    private PieChart pieChart;


    private PaginationHelper<Product> paginationHelper;

    // Service
    private ProductService productService = new ProductService();

    // List
    private ObservableList<Product> products = FXCollections.observableArrayList();

    // Map
    private Map<Product, Double> productRevenueMap = new HashMap<>();

    //Validator
    private final Validator validator = new Validator();

    @FXML
    private Label lbl_error_fromDate;

    @FXML
    private Label lbl_error_toDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        productRevenueMap = productService.getProductTopMonthSellingByRevenueFromTo(firstDayOfMonth, lastDayOfMonth);

        for (Map.Entry<Product, Double> entry : productRevenueMap.entrySet()) {
            Product product = entry.getKey();
            Double revenue = entry.getValue();
            product.setRevenue(revenue);
            products.add(product);
        }

        paginationHelper = new PaginationHelper<>();
        initTableView();

        paginationHelper.setTableView(tableView);
        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        paginationHelper.setItems(products);
        paginationHelper.showFirstPage();

        txt_fromDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_fromDate.getLocale()));
        txt_toDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_toDate.getLocale()));

        txt_fromDate.setValue(firstDayOfMonth);
        txt_toDate.setValue(lastDayOfMonth);

        initValidator();
        initPieChart();

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
        tbc_productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        tbc_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        tbc_price.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_price.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPrice()));

        tbc_revenue.setCellFactory(column -> new FormattedDoubleTableCell<>());
        tbc_revenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));
    }

    @FXML
    void viewButtonOnClick() {
        if(validator.validate()){
            LocalDate fromDate = txt_fromDate.getValue();
            LocalDate toDate = txt_toDate.getValue();
            productRevenueMap = productService.getProductTopMonthSellingByRevenueFromTo(fromDate, toDate);

            for (Map.Entry<Product, Double> entry : productRevenueMap.entrySet()) {
                Product product = entry.getKey();
                Double revenue = entry.getValue();
                product.setRevenue(revenue);
                products.add(product);
            }

            paginationHelper.setItems(products);
            paginationHelper.showFirstPage();
        }
    }

    private void initPieChart() {
        ArrayList<PieChart.Data> pieChartData = new ArrayList<>();
        pieChartData.add(new PieChart.Data("Dữ liệu A", 25.0));
        pieChartData.add(new PieChart.Data("Dữ liệu B", 40.0));
        pieChartData.add(new PieChart.Data("Dữ liệu C", 35.0));
        pieChart.getData().addAll(pieChartData);
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

}
