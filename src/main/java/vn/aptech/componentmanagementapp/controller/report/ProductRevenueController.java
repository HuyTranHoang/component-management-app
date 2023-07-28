package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.OrderDetailService;
import vn.aptech.componentmanagementapp.service.ProductService;
import vn.aptech.componentmanagementapp.util.FormattedDoubleTableCell;
import vn.aptech.componentmanagementapp.util.PaginationHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ProductRevenueController implements Initializable {

    @FXML
    private ToggleGroup tgg_type;

    @FXML
    private MFXRadioButton rbtn_byRevenue;

    @FXML
    private MFXRadioButton rbtn_bySoldAmount;

    @FXML
    private Label lbl_reportTitle;

    @FXML
    private Label lbl_sortBy;

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
    private TableColumn<Product, Integer> tbc_soldAmount;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label lbl_fromTo;

    private PaginationHelper<Product> paginationHelper;

    // Service
    private final ProductService productService = new ProductService();
    private final OrderDetailService orderDetailService = new OrderDetailService();

    // List
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    // Map
    private Map<Product, Double> productRevenueMap = new HashMap<>();
    private Map<Product, Integer> productQuantityMap = new HashMap<>();
    private Map<String, Double> totalAmountByCategory = new HashMap<>();
    private Map<String, Integer> totalQuantityByCategory = new HashMap<>();
    private final ArrayList<PieChart.Data> pieChartData = new ArrayList<>();

    //Validator
    private final Validator validator = new Validator();
    @FXML
    private Label lbl_error_fromDate;

    // Formator
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        txt_fromDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_fromDate.getLocale()));
        txt_toDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_toDate.getLocale()));

        txt_fromDate.setValue(firstDayOfMonth);
        txt_toDate.setValue(lastDayOfMonth);

        paginationHelper = new PaginationHelper<>();
        paginationHelper.setTableView(tableView);
        paginationHelper.setPageButtonContainer(pageButtonContainer);
        paginationHelper.setFirstPageButton(firstPageButton);
        paginationHelper.setPreviousButton(previousButton);
        paginationHelper.setNextButton(nextButton);
        paginationHelper.setLastPageButton(lastPageButton);

        initDataTableView(firstDayOfMonth, lastDayOfMonth);
        initPieChart(firstDayOfMonth, lastDayOfMonth);
        initValidator();
        initTableView();
        initComboBox();
        initSort();

        paginationHelper.setItems(products);
        lbl_fromTo.setText("from " + dateTimeFormatter.format(firstDayOfMonth) + " to " + dateTimeFormatter.format(lastDayOfMonth));
        paginationHelper.showFirstPage();


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

        tbc_soldAmount.setCellValueFactory(new PropertyValueFactory<>("soldAmount"));
    }

    @FXML
    public void viewButtonOnClick() {
        if(validator.validate()){
            LocalDate fromDate = txt_fromDate.getValue();
            LocalDate toDate = txt_toDate.getValue();



            RadioButton selectedRadio = (RadioButton) tgg_type.getSelectedToggle();
            String selectedType = selectedRadio.getText();


            products.clear();
            if (selectedType.equals("By revenue")) {
                productRevenueMap = productService.getProductTopSellingByRevenueFromTo(fromDate, toDate.plusDays(1));
                for (Map.Entry<Product, Double> entry : productRevenueMap.entrySet()) {
                    Product product = entry.getKey();
                    Double revenue = entry.getValue();
                    product.setRevenue(revenue);
                    products.add(product);
                }
                tbc_revenue.setVisible(true);
                tbc_soldAmount.setVisible(false);

                // Pie chart
                totalAmountByCategory = orderDetailService.getOrderDetailTotalAmountByCategory(fromDate, toDate.plusDays(1));
                pieChartData.clear();
                pieChart.getData().clear();

                double sumTotal = 0;
                for (Map.Entry<String, Double> entry : totalAmountByCategory.entrySet()) {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                    sumTotal += entry.getValue();
                }
                pieChart.getData().addAll(pieChartData);
                double finalSumTotal = sumTotal;
                pieChart.getData().forEach(data -> {
                    String percentage = String.format("%.2f%%", (data.getPieValue() / finalSumTotal * 100));
                    Tooltip tooltip = new Tooltip(percentage);
                    Tooltip.install(data.getNode(), tooltip);
                });

                lbl_reportTitle.setText("Top products revenue");
                lbl_sortBy.setText("Sort by revenue");
            } else {
                productQuantityMap = productService.getProductTopSellingByQuantityFromTo(fromDate, toDate.plusDays(1));
                for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {
                    Product product = entry.getKey();
                    Integer soldAmount = entry.getValue();
                    product.setSoldAmount(soldAmount);
                    products.add(product);
                }
                tbc_revenue.setVisible(false);
                tbc_soldAmount.setVisible(true);

                // Pie chart
                totalQuantityByCategory = orderDetailService.getOrderDetailTotalQuantityByCategory(fromDate, toDate.plusDays(1));
                pieChartData.clear();
                pieChart.getData().clear();

                double sumTotal = 0;
                for (Map.Entry<String, Integer> entry : totalQuantityByCategory.entrySet()) {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                    sumTotal += entry.getValue();
                }
                pieChart.getData().addAll(pieChartData);
                double finalSumTotal = sumTotal;
                pieChart.getData().forEach(data -> {
                    String percentage = String.format("%.2f%%", (data.getPieValue() / finalSumTotal * 100));
                    Tooltip tooltip = new Tooltip(percentage);
                    Tooltip.install(data.getNode(), tooltip);
                });

                lbl_reportTitle.setText("Top products sold amount");
                lbl_sortBy.setText("Sort by sold amount");
            }

            paginationHelper.setItems(products);
            applySorting();
            paginationHelper.showFirstPage();

            lbl_fromTo.setText("from " + dateTimeFormatter.format(fromDate) + " to " + dateTimeFormatter.format(toDate));

        }
    }

    private void initDataTableView(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        productRevenueMap = productService.getProductTopSellingByRevenueFromTo(firstDayOfMonth, lastDayOfMonth.plusDays(1));

        for (Map.Entry<Product, Double> entry : productRevenueMap.entrySet()) {
            Product product = entry.getKey();
            Double revenue = entry.getValue();
            product.setRevenue(revenue);
            products.add(product);
        }
    }

    private void initPieChart(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        totalAmountByCategory = orderDetailService.getOrderDetailTotalAmountByCategory(firstDayOfMonth, lastDayOfMonth.plusDays(1));

        double sumTotal = 0;
        for (Map.Entry<String, Double> entry : totalAmountByCategory.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            sumTotal += entry.getValue();
        }

        pieChart.getData().addAll(pieChartData);
        double finalSumTotal = sumTotal;
        pieChart.getData().forEach(data -> {
            String percentage = String.format("%.2f%%", (data.getPieValue()/ finalSumTotal * 100));
            Tooltip tooltip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), tooltip);
        });
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
        RadioButton selectedRadio = (RadioButton) tgg_type.getSelectedToggle();
        String selectedType = selectedRadio.getText();
        Comparator<Product> comparator;
        if (selectedType.equals("By revenue")) {
            comparator = Comparator.comparing(Product::getRevenue);
            if ("DESC".equals(orderBy)) {
                comparator = comparator.reversed();
            }
        } else {
            comparator = Comparator.comparing(Product::getSoldAmount);
            if ("DESC".equals(orderBy)) {
                comparator = comparator.reversed();
            }
        }

        FXCollections.sort(products, comparator);
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

}
