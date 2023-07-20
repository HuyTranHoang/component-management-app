package vn.aptech.componentmanagementapp.controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import vn.aptech.componentmanagementapp.service.OrderService;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart_totalAmount;

    @FXML
    private AnchorPane dashboardView;

    //  Service
    OrderService orderService = new OrderService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateBarChart();
    }

    public void updateBarChart() {
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Total Amount");

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Map<LocalDate, Double> weeklyTotalAmounts = orderService.getWeeklyTotalAmounts(startOfWeek, endOfWeek);

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            Double totalAmount = weeklyTotalAmounts.getOrDefault(currentDay, 0.0); // Lấy tổng totalAmount tương ứng với ngày hiện tại
            dataSeries.getData().add(new XYChart.Data<>(daysOfWeek[i], totalAmount));
        }

        barChart_totalAmount.getData().setAll(dataSeries);
    }


}
