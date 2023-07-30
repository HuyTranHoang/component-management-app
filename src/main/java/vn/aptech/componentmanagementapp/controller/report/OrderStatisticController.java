package vn.aptech.componentmanagementapp.controller.report;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.service.OrderService;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

public class OrderStatisticController implements Initializable {

    @FXML
    private Label lbl_averageRevenueCustomer;

    @FXML
    private Label lbl_averageRevenueOrder;

    @FXML
    private Label lbl_error_fromDate;

    @FXML
    private Label lbl_fromTo;

    @FXML
    private Label lbl_totalCustomer;

    @FXML
    private Label lbl_totalOrder;

    @FXML
    private Label lbl_totalOrderCanceled;

    @FXML
    private Label lbl_totalRevenue;

    @FXML
    private MFXDatePicker txt_fromDate;

    @FXML
    private MFXDatePicker txt_toDate;
    // Validator
    private Validator validator = new Validator();

    // Service
    private final OrderService orderService = new OrderService();

    // Formatter
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStatistic();
        initValidator();

    }

    private void initStatistic() {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        txt_fromDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_fromDate.getLocale()));
        txt_toDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", txt_toDate.getLocale()));

        txt_fromDate.setValue(firstDayOfMonth);
        txt_toDate.setValue(lastDayOfMonth);

        lbl_fromTo.setText("from " + dateTimeFormatter.format(firstDayOfMonth) + " to " + dateTimeFormatter.format(lastDayOfMonth));

        int countOrder = orderService.getCountOrder(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        lbl_totalOrder.setText(String.valueOf(countOrder));
        double totalAmount = orderService.getSumTotalAmount(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        lbl_totalRevenue.setText(decimalFormat.format(totalAmount));
        double averageRevenuePerOrder = totalAmount / countOrder;
        lbl_averageRevenueOrder.setText(decimalFormat.format(averageRevenuePerOrder));
        int countCustomer = orderService.getCountCustomer(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        lbl_totalCustomer.setText(String.valueOf(countCustomer));
        double averageRevenuePerCustomer = totalAmount / countCustomer;
        lbl_averageRevenueCustomer.setText(decimalFormat.format(averageRevenuePerCustomer));
        int countCanceledOrder = orderService.getCountCanceledOrder(firstDayOfMonth, lastDayOfMonth.plusDays(1));
        lbl_totalOrderCanceled.setText(String.valueOf(countCanceledOrder));
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

    @FXML
    void viewButtonOnClick() {
        if(validator.validate()){
            LocalDate fromDate = txt_fromDate.getValue();
            LocalDate toDate = txt_toDate.getValue();

            lbl_fromTo.setText("from " + dateTimeFormatter.format(fromDate) + " to " + dateTimeFormatter.format(toDate));

            int countOrder = orderService.getCountOrder(fromDate, toDate.plusDays(1));
            lbl_totalOrder.setText(String.valueOf(countOrder));
            double totalAmount = orderService.getSumTotalAmount(fromDate, toDate.plusDays(1));
            lbl_totalRevenue.setText(decimalFormat.format(totalAmount));

            double averageRevenuePerOrder = 0;
            if (countOrder != 0)
                averageRevenuePerOrder = totalAmount / countOrder;
            lbl_averageRevenueOrder.setText(decimalFormat.format(averageRevenuePerOrder));

            int countCustomer = orderService.getCountCustomer(fromDate, toDate.plusDays(1));
            lbl_totalCustomer.setText(String.valueOf(countCustomer));

            double averageRevenuePerCustomer = 0;
            if (countCustomer != 0)
                averageRevenuePerCustomer = totalAmount / countCustomer;
            lbl_averageRevenueCustomer.setText(decimalFormat.format(averageRevenuePerCustomer));

            int countCanceledOrder = orderService.getCountCanceledOrder(fromDate, toDate.plusDays(1));
            lbl_totalOrderCanceled.setText(String.valueOf(countCanceledOrder));

        }
    }

}
