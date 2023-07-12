package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagementController implements Initializable {
    @FXML
    private AnchorPane anchor_main_rightPanel;

    @FXML
    private AnchorPane anchor_mainPanel;

    @FXML
    private MFXButton btn_leftPanel_Exit;

    @FXML
    private ImageView leftPanel_imageView;

    @FXML
    private MFXButton btn_leftPanel_OrderList;

    @FXML
    private MFXButton btn_leftPanel_customerList;

    @FXML
    private MFXButton btn_leftPanel_productList;

    // Cached views
    private AnchorPane productView;
    private AnchorPane customerView;
    private AnchorPane orderView;

    //  Icon
    @FXML
    private FontIcon icon_customerList;

    @FXML
    private FontIcon icon_orderList;

    @FXML
    private FontIcon icon_productList;


    //    Variable
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product.fxml"));
            productView = fxmlLoader.load();
            ProductController controller = fxmlLoader.getController();
            controller.setAnchor_main_rightPanel(anchor_main_rightPanel);
            anchor_main_rightPanel.getChildren().add(productView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void exitButtonOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure want to exit?");

        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            DatabaseConnection.closeConnection(DatabaseConnection.getConnection());
            stage.close();
        }
    }
    @FXML
    void minimizeButtonOnClick() {
        stage.setIconified(true);
    }
    @FXML
    void productListButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product.fxml"));
                productView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/product.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        icon_productList.setIconColor(Paint.valueOf("#4A55A2"));
        icon_customerList.setIconColor(Paint.valueOf("#fff"));
        icon_orderList.setIconColor(Paint.valueOf("#fff"));

        btn_leftPanel_productList.setId("button-custom-selected");
        btn_leftPanel_customerList.setId("button-custom");
        btn_leftPanel_OrderList.setId("button-custom");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(productView);
    }

    @FXML
    void customerListButtonOnClick() {
        if (customerView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/customer/main-customer.fxml"));
                customerView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/customer.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        icon_productList.setIconColor(Paint.valueOf("#fff"));
        icon_customerList.setIconColor(Paint.valueOf("#4A55A2"));
        icon_orderList.setIconColor(Paint.valueOf("#fff"));

        btn_leftPanel_productList.setId("button-custom");
        btn_leftPanel_customerList.setId("button-custom-selected");
        btn_leftPanel_OrderList.setId("button-custom");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(customerView);
    }

    @FXML
    void orderListButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order.fxml"));
                orderView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/order.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        icon_productList.setIconColor(Paint.valueOf("#fff"));
        icon_customerList.setIconColor(Paint.valueOf("#fff"));
        icon_orderList.setIconColor(Paint.valueOf("#4A55A2"));

        btn_leftPanel_productList.setId("button-custom");
        btn_leftPanel_customerList.setId("button-custom");
        btn_leftPanel_OrderList.setId("button-custom-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }

}
