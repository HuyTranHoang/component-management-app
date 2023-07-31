package vn.aptech.componentmanagementapp.controller.order;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.service.OrderDetailService;
import vn.aptech.componentmanagementapp.util.ProductInfoViewShow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderShowController implements Initializable {

    private AnchorPane anchor_main_rightPanel;
    private AnchorPane orderView;

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    public void setOrderView(AnchorPane orderView) {
        this.orderView = orderView;
    }

    private Order currentOrder;

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    private List<OrderDetail> orderDetails = new ArrayList<>();

    //  Service
    OrderDetailService orderDetailService = new OrderDetailService();

    @FXML
    private HBox hbox_updateButtonGroup;

    @FXML
    private Label lbl_EmployeeName;

    @FXML
    private Label lbl_customerName;

    @FXML
    private Label lbl_customerPhone;

    @FXML
    private Label lbl_deliveryDate;

    @FXML
    private Label lbl_deliveryLocation;

    @FXML
    private Label lbl_note;

    @FXML
    private Label lbl_orderDate;

    @FXML
    private Label lbl_receiveDate;

    @FXML
    private Label lbl_totalAmount;

    @FXML
    private VBox vbox_orderDetail;

    private DecimalFormat decimalFormat = new DecimalFormat("#,##0₫");

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void setInformation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Customer currentCustomer = currentOrder.getCustomer();
        Employee currentEmployee = currentOrder.getEmployee();

        lbl_orderDate.setText(formatter.format(currentOrder.getOrderDate()));
        lbl_deliveryDate.setText(formatter.format(currentOrder.getDeliveryDate()));
        lbl_receiveDate.setText(formatter.format(currentOrder.getReceiveDate()));

        lbl_customerName.setText(currentCustomer.getName());
        lbl_customerPhone.setText(currentCustomer.getPhone());

        lbl_EmployeeName.setText(currentEmployee.getName());

        lbl_deliveryLocation.setText(currentOrder.getDeliveryLocation());
        lbl_note.setText(currentOrder.getNote());

        clearOrderDetail();
        orderDetails = orderDetailService.getAllOrderDetailByOrderId(currentOrder.getId());
        double totalAmount = 0;

        for (OrderDetail orderDetail: orderDetails) {
            ProductInfoViewShow productInfoView = new ProductInfoViewShow();
            productInfoView.getLblProductName().setText(orderDetail.getName());
            productInfoView.getLblProductPrice().setText(decimalFormat.format(orderDetail.getPrice()));
            productInfoView.getLblProductDiscount().setText(String.valueOf(orderDetail.getDiscount()));
            productInfoView.getLblProductQuantity().setText(String.valueOf(orderDetail.getQuantity()));
            productInfoView.getLblProductTotalAmount().setText(decimalFormat.format(orderDetail.getTotalAmount()));
            productInfoView.setVbox_orderDetail(vbox_orderDetail);
            productInfoView.setOrderDetail(orderDetail);
            productInfoView.setOrderDetails(orderDetails);

            totalAmount += orderDetail.getTotalAmount();

            vbox_orderDetail.getChildren().add(productInfoView);
        }
        lbl_totalAmount.setText(decimalFormat.format(totalAmount));
    }

    @FXML
    void clearOrderDetail() {
        vbox_orderDetail.getChildren().clear();
        orderDetails.clear();
        lbl_totalAmount.setText("");
    }

    @FXML
    void listOrderButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/product/order.fxml"));
                orderView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }

    private void exportOrderToPDF(File file) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

//            PDType0Font font = PDType0Font.load(document, new File("src/main/resources/vn/aptech/componentmanagementapp/font/Roboto-Regular.ttf"));
//            contentStream.setFont(font, 12);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                PDType0Font font = PDType0Font.load(document, new File("src/main/resources/vn/aptech/componentmanagementapp/font/Roboto-Regular.ttf"));
                contentStream.setFont(font, 12);
                contentStream.setLeading(16.0f);
                contentStream.newLineAtOffset(25, PDRectangle.A4.getHeight() - 50);
                // Write order information
                contentStream.showText("Order Date: " + formatDate(currentOrder.getOrderDate()));
                contentStream.newLine();
                contentStream.showText("Delivery Location: " + currentOrder.getDeliveryLocation());
                contentStream.newLine();
                contentStream.showText("____________________________________________");
                contentStream.newLine();
                contentStream.setFont(font, 12);
                contentStream.showText("Customer name: " + currentOrder.getCustomer().getName());
                contentStream.newLine();
                contentStream.showText("Customer phone: " + currentOrder.getCustomer().getPhone());
                contentStream.newLine();
                contentStream.newLine();
                // Write order details table header
                contentStream.showText("Order Details:");
                contentStream.newLine();

                int index = 1;

                contentStream.setFont(font, 10);

                for (OrderDetail detail : orderDetails) {
                    contentStream.showText(index + ". " + detail.getProduct().getName());
                    contentStream.newLine();

                    contentStream.showText(" ".repeat(25) + "Quantity: " + detail.getQuantity() + " --- Total: " + decimalFormat.format(detail.getTotalAmount()));
                    contentStream.newLine();
                    index++;
                }

                contentStream.newLine();
                contentStream.setFont(font, 14);
                contentStream.showText("Total Amount: " + decimalFormat.format(currentOrder.getTotalAmount()));
                contentStream.newLine();


                contentStream.endText();
            }


            document.save(file);
            System.out.println("Data exported to PDF successfully.");
        } catch (IOException ex) {
            System.err.println("Error exporting data to PDF: " + ex.getMessage());
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }


    @FXML
    void exportButtonOnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(vbox_orderDetail.getScene().getWindow());

        if (selectedFile != null) {
            exportOrderToPDF(selectedFile);
        }
    }
}
