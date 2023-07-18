package vn.aptech.componentmanagementapp.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vn.aptech.componentmanagementapp.model.OrderDetail;

import java.util.List;
import java.util.Optional;

public class ProductInfoView extends VBox {
    private final Label lblProductName;
    private final Label lblProductPrice;
    private final Label lblProductDiscount;
    private final Label lblProductTotalAmount;
    private final Label lblProductQuantity;

    private Button btnEdit;
    private Button btnRemove;

    private VBox vbox_orderDetail;

    public void setVbox_orderDetail(VBox vbox_orderDetail) {
        this.vbox_orderDetail = vbox_orderDetail;
    }

    private OrderDetail orderDetail;

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    private List<OrderDetail> orderDetails;

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Label getLblProductName() {
        return lblProductName;
    }

    public Label getLblProductPrice() {
        return lblProductPrice;
    }

    public Label getLblProductDiscount() {
        return lblProductDiscount;
    }

    public Label getLblProductTotalAmount() {
        return lblProductTotalAmount;
    }

    public Label getLblProductQuantity() {
        return lblProductQuantity;
    }

    public ProductInfoView() {
        setSpacing(15);
        setPadding(new Insets(10));
        setStyle("-fx-border-width: 0 0 2px 0; -fx-border-color: black");

        lblProductName = new Label();
        lblProductName.setWrapText(true);
        lblProductPrice = new Label();
        lblProductDiscount = new Label();
        lblProductQuantity = new Label();
        lblProductTotalAmount = new Label();

        btnEdit = new Button("Edit");
        btnRemove = new Button("Remove");

        // Đặt sự kiện cho nút Remove
        btnRemove.setOnAction(event -> {
            // Xoá sản phẩm khỏi vbox_orderDetail
            vbox_orderDetail.getChildren().remove(this);
            orderDetails.remove(orderDetail);
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Set column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(100);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);

        gridPane.add(createProductName("Product name:", lblProductName), 0, 0, 3, 1);

        gridPane.add(createLabelHBox("Price:", lblProductPrice), 0, 1);
        gridPane.add(createLabelHBox("Quantity:", lblProductQuantity), 1, 1);
        gridPane.add(btnEdit, 2, 1);

        gridPane.add(createLabelHBox("Discount (%):", lblProductDiscount), 0, 2);
        gridPane.add(createLabelHBox("Total:", lblProductTotalAmount), 1, 2);
        gridPane.add(btnRemove, 2, 2);


        getChildren().add(gridPane);
    }

    private VBox createProductName(String labelText, Label label) {
        Label lblLabel = new Label(labelText);
        lblLabel.setFont(Font.font("Inter Bold", 15));
        lblLabel.setTextFill(javafx.scene.paint.Color.valueOf("#4a55a2"));
        lblLabel.setWrapText(true);

        Text text = new Text();
        text.setFont(Font.font("Inter Regular", 13));
        text.setWrappingWidth(450);
        text.textProperty().bind(label.textProperty());

        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(lblLabel, text);
        return vbox;
    }

    private VBox createLabelHBox(String labelText, Label label) {
        Label lblLabel = new Label(labelText);
        lblLabel.setFont(Font.font("Inter Bold", 15));
        lblLabel.setTextFill(javafx.scene.paint.Color.valueOf("#4a55a2"));
        lblLabel.setWrapText(true);

        label.setFont(Font.font("Inter Regular", 13));
        label.setWrapText(true);

        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(lblLabel, label);
        return vbox;
    }

    public void setEditButtonAction(EventHandler<ActionEvent> eventHandler) {
        btnEdit.setOnAction(eventHandler);
    }
}
