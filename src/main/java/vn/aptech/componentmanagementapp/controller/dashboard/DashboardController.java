package vn.aptech.componentmanagementapp.controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import vn.aptech.componentmanagementapp.service.CustomerService;
import vn.aptech.componentmanagementapp.service.OrderService;
import vn.aptech.componentmanagementapp.service.ProductService;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart_totalAmount;

    @FXML
    private AnchorPane dashboardView;

    @FXML
    private Label lbl_newCustomer;

    @FXML
    private Label lbl_newOrder;

    @FXML
    private Label lbl_newProduct;

    //  Service
    OrderService orderService = new OrderService();
    CustomerService customerService = new CustomerService();
    ProductService productService = new ProductService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateBarChart();
        updateWeeklySummery();
    }

    public void updateBarChart() {
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Total Amount");

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

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



}
