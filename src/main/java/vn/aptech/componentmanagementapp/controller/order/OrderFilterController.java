package vn.aptech.componentmanagementapp.controller.order;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrderFilterController implements Initializable, OrderAddSelectCustomerController.CustomerSelectionCallback,
    OrderAddSelectEmployeeController.EmployeeSelectionCallback{

    public interface ViewResultCallback {
        void onViewResultClicked(List<Order> filterOrder);
    }

    private ViewResultCallback viewResultCallback;

    public void setViewResultCallback(ViewResultCallback callback) {
        this.viewResultCallback = callback;
    }

    public interface ClearFilterCallback {
        void onClearFilterClicked();
    }

    private ClearFilterCallback clearFilterCallback;

    public void setClearFilterCallback(ClearFilterCallback callback) {
        this.clearFilterCallback = callback;
    }

    // Cache view
    private Scene selectCustomerScene;
    private Stage selectCustomerStage;

    private Scene selectEmployeeScene;
    private Stage selectEmployeeStage;

    // Filter var
    private Customer currentCustomer;

    private Employee currentEmployee;

    @FXML
    private VBox vbox_customerInfo;

    @FXML
    private VBox vbox_employeeInfo;

    @FXML
    private MFXToggleButton btn_toggleDate;

    @FXML
    private MFXComboBox<String> cbb_byTypeOfDate;

    @FXML
    private MFXDatePicker txt_fromDate;

    @FXML
    private MFXDatePicker txt_toDate;

    @FXML
    private Label lbl_error_from;

    @FXML
    private Label lbl_customerName;

    @FXML
    private Label lbl_customerPhone;

    @FXML
    private Label lbl_employeeName;

    @FXML
    private Label lbl_employeePhone;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Label filter_noti_label; // Truyền từ ngoài vào
    private Circle filter_noti_shape;

    public void setFilter_noti_label(Label filter_noti_label) {
        this.filter_noti_label = filter_noti_label;
    }

    public void setFilter_noti_shape(Circle filter_noti_shape) {
        this.filter_noti_shape = filter_noti_shape;
    }

    //    List
    private ObservableList<Order> orders;

    public void setOrders(ObservableList<Order> orders) {
        this.orders = orders;
    }

    Validator validator = new Validator();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> typeOfDate =
                FXCollections.observableArrayList(List.of("Order date", "Delivery date", "Receive date"));
        cbb_byTypeOfDate.setItems(typeOfDate);
        cbb_byTypeOfDate.selectFirst();
        txt_fromDate.setValue(LocalDate.now());
        txt_toDate.setValue(LocalDate.now());

        initValidator();
    }

    private void initValidator() {
        validator.createCheck()
                .dependsOn("from", txt_fromDate.valueProperty())
                .dependsOn("to", txt_toDate.valueProperty())
                .withMethod(context -> {
                    LocalDate from = context.get("from");
                    LocalDate to = context.get("to");
                    if (from.isAfter(to) && btn_toggleDate.isSelected()) {
                        context.error("'From' date must be before 'to' date");
                    }
                })
                .decoratingWith(this::labelDecorator)
                .decorates(lbl_error_from);
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
    void clearFilterButtonOnClick() {
        cbb_byTypeOfDate.selectFirst();
        txt_fromDate.setValue(LocalDate.now());
        txt_toDate.setValue(LocalDate.now());


        filter_noti_label.setVisible(false);
        filter_noti_shape.setVisible(false);

        currentCustomer = null;
        vbox_customerInfo.setVisible(false);

        currentEmployee = null;
        vbox_employeeInfo.setVisible(false);

        if (clearFilterCallback != null) {
            clearFilterCallback.onClearFilterClicked();
        }

        stage.close();
    }

    @FXML
    void viewResultButtonOnClick() {
        if (validator.validate()) {
            List<Order> filterOrder = orders;
            int countFilter = 0;

            if (btn_toggleDate.isSelected()) {
                LocalDate fromDate = txt_fromDate.getValue();
                LocalDate toDate = txt_toDate.getValue();
                String typeDate = cbb_byTypeOfDate.getSelectedItem();

                switch (typeDate) {
                    case "Order date" -> filterOrder = filterOrder.stream()
                            .filter(order -> {
                                LocalDate orderDate = order.getOrderDate().toLocalDate();
                                return orderDate.isEqual(fromDate) || orderDate.isEqual(toDate)
                                        || (orderDate.isAfter(fromDate) && orderDate.isBefore(toDate));
                            })
                            .collect(Collectors.toList());
                    case "Delivery date" -> filterOrder = filterOrder.stream()
                            .filter(order -> {
                                LocalDate deliveryDate = order.getDeliveryDate().toLocalDate();
                                return deliveryDate.isEqual(fromDate) || deliveryDate.isEqual(toDate)
                                        || (deliveryDate.isAfter(fromDate) && deliveryDate.isBefore(toDate));
                            })
                            .collect(Collectors.toList());
                    default -> filterOrder = filterOrder.stream()
                            .filter(order -> {
                                LocalDate receiveDate = order.getReceiveDate().toLocalDate();
                                return receiveDate.isEqual(fromDate) || receiveDate.isEqual(toDate)
                                        || (receiveDate.isAfter(fromDate) && receiveDate.isBefore(toDate));
                            })
                            .collect(Collectors.toList());
                }
                countFilter++;
            }

            if (currentCustomer != null) {
                filterOrder = filterOrder.stream()
                        .filter(order -> order.getCustomerId() == currentCustomer.getId())
                        .collect(Collectors.toList());
                countFilter++;
            }

            if (currentEmployee != null) {
                filterOrder = filterOrder.stream()
                        .filter(order -> order.getEmployeeId() == currentEmployee.getId())
                        .collect(Collectors.toList());
                countFilter++;
            }

            if (viewResultCallback != null) {
                viewResultCallback.onViewResultClicked(filterOrder);
            }

            if (countFilter > 0 ) {
                filter_noti_shape.setVisible(true);
                filter_noti_label.setVisible(true);
                filter_noti_label.setText(String.valueOf(countFilter));
            } else {
                filter_noti_shape.setVisible(false);
                filter_noti_label.setVisible(false);
            }

            stage.close();
        }
    }

    @FXML
    void selectCustomerOnClick() {
        try {
            if (selectCustomerScene == null && selectCustomerStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order-add-selectCustomer.fxml"));
                selectCustomerScene = new Scene(fxmlLoader.load());
                selectCustomerStage = new Stage();
                OrderAddSelectCustomerController controller = fxmlLoader.getController();
                controller.setCustomerSelectionCallback(this);
                controller.setStage(selectCustomerStage);
                selectCustomerStage.setTitle("Select customer");
                selectCustomerStage.initModality(Modality.APPLICATION_MODAL);
                selectCustomerStage.setResizable(false);

                selectCustomerStage.setScene(selectCustomerScene);
            }

            selectCustomerStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void selectEmployeeOnClick() {
        try {
            if (selectEmployeeScene == null && selectEmployeeStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order-add-selectEmployee.fxml"));
                selectEmployeeScene = new Scene(fxmlLoader.load());
                selectEmployeeStage = new Stage();
                OrderAddSelectEmployeeController controller = fxmlLoader.getController();
                controller.setEmployeeSelectionCallback(this);
                controller.setStage(selectEmployeeStage);
                selectEmployeeStage.setTitle("Select customer");
                selectEmployeeStage.initModality(Modality.APPLICATION_MODAL);
                selectEmployeeStage.setResizable(false);

                selectEmployeeStage.setScene(selectEmployeeScene);
            }

            selectEmployeeStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        currentCustomer = customer;
        lbl_customerName.setText(currentCustomer.getName());
        lbl_customerPhone.setText(currentCustomer.getPhone());
        vbox_customerInfo.setVisible(true);
    }

    @Override
    public void onEmployeeSelected(Employee employee) {
        currentEmployee = employee;
        lbl_employeeName.setText(currentEmployee.getName());
        lbl_employeePhone.setText(currentEmployee.getPhone());
        vbox_employeeInfo.setVisible(true);
    }
}
