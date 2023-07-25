package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.controller.dashboard.DashboardController;
import vn.aptech.componentmanagementapp.controller.employee.EmployeeController;
import vn.aptech.componentmanagementapp.controller.order.OrderController;
import vn.aptech.componentmanagementapp.controller.product.ProductController;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ManagementController implements Initializable {
    @FXML
    private AnchorPane anchor_main_rightPanel;

    @FXML
    private ImageView leftPanel_imageView;

    @FXML
    private MFXButton btn_leftPanel_orderList;

    @FXML
    private MFXButton btn_leftPanel_customerList;

    @FXML
    private MFXButton btn_leftPanel_productList;

    @FXML
    private MFXButton btn_leftPanel_employeeList;

    @FXML
    private MFXButton btn_leftPanel_dashboard;

    @FXML
    private MFXButton btn_leftPanel_supplierList;

    @FXML
    private MFXButton btn_leftPanel_categoryList;

    @FXML
    private MFXButton btn_leftPanel_reportList;

    @FXML
    private HBox hbox_employeeInfo;

    @FXML
    private Circle circle_avatar;

    @FXML
    private MenuButton menu_employeeName;

    // Cached views

    LoginController loginController;

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    private AnchorPane loginView;

    public void setLoginView(AnchorPane loginView) {
        this.loginView = loginView;
    }

    private AnchorPane dashboardView;
    private AnchorPane productView;
    private AnchorPane employeeView;
    private AnchorPane customerView;
    private AnchorPane orderView;
    private AnchorPane categoryView;
    private AnchorPane supplierView;
    private AnchorPane reportView;
    // Controller
    private DashboardController dashboardController;
    private ProductController productController;
    private EmployeeController employeeController;
    private OrderController orderController;


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
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/dashboard/dashboard.fxml"));
            dashboardView = fxmlLoader.load();
            dashboardController = fxmlLoader.getController();
            Platform.runLater(() -> {
                dashboardController.setNameDate(currentEmployee.getName(), LocalDate.now());
                setAvatar(currentEmployee.getImage());
                menu_employeeName.setText(currentEmployee.getName());
            });
            anchor_main_rightPanel.getChildren().add(dashboardView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setAvatar(String filename) {
        if (filename != null && !filename.isEmpty()) {
            String imagePath = "images/employee/" + filename;
            File file = new File(imagePath);

            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                circle_avatar.setFill(new ImagePattern(image));
            } else {
                String defaultImagePath = "images/employee/defaultImg.jpg";
                Image defaultImage = new Image(new File(defaultImagePath).toURI().toString());
                circle_avatar.setFill(new ImagePattern(defaultImage));
            }
        } else {
            String defaultImagePath = "images/employee/defaultImg.jpg";
            Image defaultImage = new Image(new File(defaultImagePath).toURI().toString());
            circle_avatar.setFill(new ImagePattern(defaultImage));
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
    void logoutButtonOnClick() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure want to logout?");

        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            loginController.clearInput();
            stage.setScene(loginView.getScene());
            stage.centerOnScreen();
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
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/dashboard/dashboard.fxml"));
                dashboardView = fxmlLoader.load();
                dashboardController = fxmlLoader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/dashboard.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        dashboardController.updateBarChart();
        dashboardController.updateWeeklySummery();
        dashboardController.updateBarChartTopSelling();

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel-selected");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(dashboardView);
    }
    @FXML
    void productListButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/product.fxml"));
                productView = fxmlLoader.load();
                productController = fxmlLoader.getController();
                productController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                productController.setProductView(productView);
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
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(productView);
    }

    @FXML
    void employeeListButtonOnClick() {
        if (employeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/employee/employee.fxml"));
                employeeView = fxmlLoader.load();
                employeeController = fxmlLoader.getController();
                employeeController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                employeeController.setEmployeeView(employeeView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;

        URL resourceURL = ComponentManagementApplication.class.getResource("images/employee.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        employeeController.reloadEmployee(); // reload lại query mỗi lần quay lại
        employeeController.resetFilterIconClicked(); // reset lại filter

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(employeeView);
    }

    @FXML
    void customerListButtonOnClick() {
        if (customerView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/customer/customer.fxml"));
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
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(customerView);
    }

    @FXML
    void orderListButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/order/order.fxml"));
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
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(orderView);
    }
    @FXML
    void supplierListButtonOnClick() {
        if (supplierView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/supplier/supplier.fxml"));
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
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(supplierView);
    }
    @FXML
    void categoryListButtonOnClick() {
        if (categoryView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/category/category.fxml"));
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
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(categoryView);
    }

    @FXML
    void reportListButtonOnClick() {
        if (reportView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/report/report.fxml"));
                reportView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Image image = null;


        URL resourceURL = ComponentManagementApplication.class.getResource("images/report.png");

        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(reportView);
    }

    public void disableEmployee() {
        btn_leftPanel_employeeList.setVisible(false);
    }

}
