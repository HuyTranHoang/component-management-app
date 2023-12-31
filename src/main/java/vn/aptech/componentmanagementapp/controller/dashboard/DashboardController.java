package vn.aptech.componentmanagementapp.controller.dashboard;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.service.CustomerService;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.service.ProductService;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class DashboardController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart_totalAmount;

    @FXML
    private BarChart<Number, String> barChart_topSelling;

    @FXML
    private Label lbl_newCustomer;

    @FXML
    private Label lbl_newOrder;

    @FXML
    private Label lbl_newProduct;

    @FXML
    private Label lbl_todayCompare;

    @FXML
    private Label lbl_todaySaleAmount;

    @FXML
    private Label lbl_date;

    @FXML
    private Label lbl_employeeName;

    @FXML
    private Circle circle_saleCompare;

    @FXML
    private FontIcon icon_saleCompare_down;

    @FXML
    private FontIcon icon_saleCompare_up;

    @FXML
    private MFXComboBox<String> cbb_topSellingType;

    @FXML
    private NumberAxis axis_quantity;


    //  Service
    OrderService orderService = new OrderService();
    CustomerService customerService = new CustomerService();
    ProductService productService = new ProductService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateBarChart();

        updateWeeklySummery();

        initComboBoxTopSelling();
        Platform.runLater(this::updateBarChartTopSelling);

        updateTodayCompare();

        axis_quantity.setTickLabelFormatter(axisFomatter);
    }

    public void updateBarChart() {
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Total Amount");

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(7);

        Map<LocalDate, Double> weeklyTotalAmounts = orderService.getWeeklyTotalAmounts(startOfWeek, endOfWeek);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        String[] daysOfWeek = new String[7];
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            daysOfWeek[i] = currentDay.format(dateFormatter);
        }

        for (int i = 0; i < daysOfWeek.length; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            Double totalAmount = weeklyTotalAmounts.getOrDefault(currentDay, 0.0);
            dataSeries.getData().add(new XYChart.Data<>(daysOfWeek[i], totalAmount));
        }

        barChart_totalAmount.getData().setAll(dataSeries);
    }

    public void updateWeeklySummery() {
        int weeklyNewCustomer = customerService.getWeeklyNewCustomer();
        lbl_newCustomer.setText(weeklyNewCustomer + " customers");

        int weeklyNewProduct = productService.getWeeklyNewProduct();
        lbl_newProduct.setText(weeklyNewProduct + " products");

        int weeklyNewOrder = orderService.getWeeklyNewOrder();
        lbl_newOrder.setText(weeklyNewOrder + " orders");

    }

    private void initComboBoxTopSelling() {
        ObservableList<String> list = FXCollections.observableArrayList(List.of("by quantity", "by revenue"));
        cbb_topSellingType.setItems(list);
        cbb_topSellingType.selectFirst();

        cbb_topSellingType.valueProperty().addListener((observable, oldValue, newValue) -> updateBarChartTopSelling());
    }

    public void updateBarChartTopSelling() {
        String sortBy = cbb_topSellingType.getValue();

        if (sortBy.equals("by quantity")) {
            Map<Product, Integer> productTopSellingMap = productService.getProductTopMonthSellingByQuantity();

            barChart_topSelling.getData().clear();

            XYChart.Series<Number, String> dataSeries = new XYChart.Series<>();

            for (Map.Entry<Product, Integer> entry : productTopSellingMap.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                dataSeries.getData().add(new XYChart.Data<>(quantity, product.getName()));
            }

            dataSeries.getData().sort(Comparator.comparingDouble(value -> value.getXValue().intValue()));

//            dataSeries.getData().sort((data1, data2) -> {
//                int xValue1 = data1.getXValue().intValue();
//                int xValue2 = data2.getXValue().intValue();
//                return Integer.compare(xValue1, xValue2);
//            });

            barChart_topSelling.getData().add(dataSeries);
        } else {
            Map<Product, Double> productTopSellingMap = productService.getProductTopMonthSellingByRevenue();

            barChart_topSelling.getData().clear();

            XYChart.Series<Number, String> dataSeries = new XYChart.Series<>();

            for (Map.Entry<Product, Double> entry : productTopSellingMap.entrySet()) {
                Product product = entry.getKey();
                Double revenue = entry.getValue();
                dataSeries.getData().add(new XYChart.Data<>(revenue, product.getName()));
            }

            dataSeries.getData().sort(Comparator.comparingDouble(value -> value.getXValue().doubleValue()));

//            dataSeries.getData().sort((data1, data2) -> {
//                double xValue1 = data1.getXValue();
//                double xValue2 = data2.getXValue();
//                return Double.compare(xValue1, xValue2);
//            });

            barChart_topSelling.getData().add(dataSeries);
        }
    }

    StringConverter<Number> axisFomatter = new StringConverter<Number>() {
        @Override
        public String toString(Number axisValue) {
            double value = axisValue.doubleValue();

            if (value >= 1e9) {
                return (value / 1e9) + " bn";
            } else if (value >= 1e6) {
                return (value / 1e6) + " M";
            } else {
                return axisValue.toString();
            }
        }

        @Override
        public Number fromString(String string) {
            return null;
        }
    };

    public void updateTodayCompare() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");
        DecimalFormat compareDecimalFormat = new DecimalFormat("#,##0.00'%'");

        double todayTotalAmount = orderService.getTodayTotalAmount();
        lbl_todaySaleAmount.setText(decimalFormat.format(todayTotalAmount));

        double yesterdayTotalAmount = orderService.getYesterdayTotalAmount();


        if (yesterdayTotalAmount != 0) {
            double percentChange = calculatePercentageChange(todayTotalAmount, yesterdayTotalAmount);
            String formattedPercentage;
            if (percentChange >= 0) {
                formattedPercentage = "+" + compareDecimalFormat.format(percentChange);
                circle_saleCompare.setFill(Paint.valueOf("#eaf8f1"));
                lbl_todayCompare.setTextFill(Paint.valueOf("#45d98c"));
                icon_saleCompare_up.setVisible(true);
                icon_saleCompare_down.setVisible(false);
            } else {
                formattedPercentage = compareDecimalFormat.format(percentChange);
                circle_saleCompare.setFill(Paint.valueOf("#f7ebeb"));
                lbl_todayCompare.setTextFill(Paint.valueOf("#ff7474"));
                icon_saleCompare_up.setVisible(false);
                icon_saleCompare_down.setVisible(true);
            }

            lbl_todayCompare.setText(formattedPercentage);
        } else {
            lbl_todayCompare.setText("No data");
        }

    }

    public double calculatePercentageChange(double todayTotalAmount, double yesterdayTotalAmount) {
        return (todayTotalAmount - yesterdayTotalAmount) / yesterdayTotalAmount * 100;
    }

    public void setNameDate(String name, LocalDate date) {
        lbl_employeeName.setText(name);

        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy", locale);
        String formattedDate = date.format(dateFormatter);

        lbl_date.setText(formattedDate);
    }

}
