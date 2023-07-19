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
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagementController implements Initializable {
    @FXML
    private AnchorPane anchor_main_rightPanel;

    @FXML
    private ImageView leftPanel_imageView;

    @FXML
    private MFXButton btn_leftPanel_OrderList;

    @FXML
    private MFXButton btn_leftPanel_customerList;

    @FXML
    private MFXButton btn_leftPanel_productList;

    @FXML
    private MFXButton btn_leftPanel_dashboard;

    @FXML
    private MFXButton btn_leftPanel_SupplierList;

    @FXML
    private MFXButton btn_leftPanel_CategoryList;

    // Cached views
    private AnchorPane dashboardView;
    private AnchorPane productView;
    private AnchorPane customerView;
    private AnchorPane orderView;
    private AnchorPane categoryView;
    private AnchorPane supplierView;

    // Controller
    private ProductController productController;
    private  OrderController orderController;


    //    Variable
    private Employee currentEmployee;
    private Stage stage;

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/dashboard/main-dashboard.fxml"));
            dashboardView = fxmlLoader.load();
            anchor_main_rightPanel.getChildren().add(dashboardView);
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
    void dashboardButtonOnClick() {
        if (dashboardView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/dashboard/main-dashboard.fxml"));
                dashboardView = fxmlLoader.load();
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

        btn_leftPanel_dashboard.setId("button-custom-mainPanel-selected");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(dashboardView);
    }
    @FXML
    void productListButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product.fxml"));
                productView = fxmlLoader.load();
                productController = fxmlLoader.getController();
                productController.setAnchor_main_rightPanel(anchor_main_rightPanel);
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

        productController.reloadProduct(); // reload lại query mỗi lần quay lại
        productController.resetFilterIconClicked(); // reset lại filter

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel");

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

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(customerView);
    }

    @FXML
    void orderListButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/main-order.fxml"));
                orderView = fxmlLoader.load();
                orderController = fxmlLoader.getController();
                orderController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderController.setCurrentEmployee(currentEmployee);
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

        orderController.reloadOrder();
        orderController.resetFilterIconClicked();

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }
    @FXML
    void supplierListButtonOnClick() {
        if (supplierView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/supplier/main-supplier.fxml"));
                supplierView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/supplier.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(supplierView);
    }
    @FXML
    void categoryListButtonOnClick() {
        if (categoryView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/category/main-category.fxml"));
                categoryView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;


        URL resourceURL = ComponentManagementApplication.class.getResource("images/category.png");

        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_OrderList.setId("button-custom-mainPanel");
        btn_leftPanel_SupplierList.setId("button-custom-mainPanel");
        btn_leftPanel_CategoryList.setId("button-custom-mainPanel-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(categoryView);
    }

}
