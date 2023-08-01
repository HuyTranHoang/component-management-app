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
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.controller.dashboard.DashboardController;
import vn.aptech.componentmanagementapp.controller.employee.EmployeeController;
import vn.aptech.componentmanagementapp.controller.order.OrderController;
import vn.aptech.componentmanagementapp.controller.product.ProductController;
import vn.aptech.componentmanagementapp.controller.report.EmployeeSalaryController;
import vn.aptech.componentmanagementapp.controller.report.ProductRevenueController;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;
import vn.aptech.componentmanagementapp.util.SetImageAlert;

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
    private MFXButton btn_leftPanel_reportListHR;

    @FXML
    private MFXButton btn_subMenu_employee;

    @FXML
    private MFXButton btn_subMenu_order;

    @FXML
    private MFXButton btn_subMenu_productQuantity;

    @FXML
    private MFXButton btn_subMenu_productRevenue;

    @FXML
    private MFXButton btn_subMenu_employeeHR;

    @FXML
    private MFXButton btn_subMenu_orderHR;

    @FXML
    private MFXButton btn_subMenu_productQuantityHR;

    @FXML
    private MFXButton btn_subMenu_productRevenueHR;

    @FXML
    private HBox hbox_employeeInfo;

    @FXML
    private VBox vbox_report_subMenu;

    @FXML
    private FontIcon icon_reportDown;

    @FXML
    private FontIcon icon_reportRight;

    @FXML
    private VBox vbox_report_subMenuHR;

    @FXML
    private FontIcon icon_reportDownHR;

    @FXML
    private FontIcon icon_reportRightHR;

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
    private AnchorPane reportProductQuantityView;
    private AnchorPane reportProductRevenueView;
    private AnchorPane reportOrderView;
    private AnchorPane reportEmployeeView;

    @FXML
    private AnchorPane anchor_reportArrowSales;
    @FXML
    private AnchorPane anchor_reportArrowHR;

    @FXML
    private VBox vbox_sales;
    @FXML
    private VBox vbox_HR;

    // Change Password Panel
    private Scene changePasswordScene;
    private Stage changePasswordStage;
    // Controller
    private DashboardController dashboardController;
    private ProductController productController;
    private EmployeeController employeeController;
    private OrderController orderController;

    private ProductRevenueController productRevenueController;
    private EmployeeSalaryController employeeSalaryController;


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
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/dashboard/dashboard.fxml"));
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

        anchor_reportArrowSales.visibleProperty().bind(vbox_sales.visibleProperty());
        anchor_reportArrowHR.visibleProperty().bind(vbox_HR.visibleProperty());
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

        SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);

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

        SetImageAlert.setIconAlert(confirmation, SetImageAlert.CONFIRMATION);

        if (confirmation.showAndWait().orElse(null) == ButtonType.OK) {
            loginController.clearInput();
            stage.setScene(loginView.getScene());
            stage.centerOnScreen();
        }
    }


    @FXML
    void changePasswordButtonOnClick() {
        try {
            if (changePasswordScene == null && changePasswordStage == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/change-password.fxml"));
                changePasswordScene = new Scene(fxmlLoader.load());
                changePasswordStage = new Stage();
                changePasswordStage.setTitle("Change Password");
                changePasswordStage.initModality(Modality.APPLICATION_MODAL);
                changePasswordStage.setScene(changePasswordScene);
                changePasswordStage.setResizable(false);

                Platform.runLater(() -> {
                    ChangePasswordController changePasswordController = fxmlLoader.getController();
                    changePasswordController.setCurrentEmployee(currentEmployee);
                    changePasswordController.setStage(changePasswordStage);
                });

                Image image = null;
                URL resourceURL = ComponentManagementApplication.class.getResource("images/password.png");
                if (resourceURL != null) {
                    String resourcePath = resourceURL.toExternalForm();
                    image = new Image(resourcePath);
                }
                changePasswordStage.getIcons().add(image);
                changePasswordStage.initModality(Modality.APPLICATION_MODAL);
            }
            changePasswordStage.show();
        } catch (IOException e) {
                throw new RuntimeException(e);
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
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/dashboard/dashboard.fxml"));
                dashboardView = fxmlLoader.load();
                dashboardController = fxmlLoader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/dashboard.png");

        dashboardController.updateBarChart();
        dashboardController.updateTodayCompare();
        dashboardController.updateWeeklySummery();
        dashboardController.updateBarChartTopSelling();


        resetSubmenu();

        btn_leftPanel_dashboard.setId("button-custom-mainPanel-selected");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(dashboardView);
    }
    @FXML
    void productButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/product/product.fxml"));
                productView = fxmlLoader.load();
                productController = fxmlLoader.getController();
                productController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                productController.setProductView(productView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/product.png");

        productController.reloadProduct(); // reload lại query mỗi lần quay lại
        productController.resetFilterIconClicked(); // reset lại filter
        productController.reloadFilter();

        resetSubmenu();

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
    void employeeButtonOnClick() {
        if (employeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/employee/employee.fxml"));
                employeeView = fxmlLoader.load();
                employeeController = fxmlLoader.getController();
                employeeController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                employeeController.setEmployeeView(employeeView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/employee.png");

        employeeController.reloadEmployee(); // reload lại query mỗi lần quay lại
        employeeController.resetFilterIconClicked(); // reset lại filter

        resetSubmenu();

//        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
//        btn_leftPanel_productList.setId("button-custom-mainPanel");
//        btn_leftPanel_employeeList.setId("button-custom-mainPanel-selected");
//        btn_leftPanel_customerList.setId("button-custom-mainPanel");
//        btn_leftPanel_orderList.setId("button-custom-mainPanel");
//        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
//        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
//        btn_leftPanel_reportList.setId("button-custom-mainPanel");

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel");


        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(employeeView);
    }

    @FXML
    void customerButtonOnClick() {
        if (customerView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/customer/customer.fxml"));
                customerView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/customer.png");

        resetSubmenu();

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
    void orderButtonOnClick() {
        if (orderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/order/order.fxml"));
                orderView = fxmlLoader.load();
                orderController = fxmlLoader.getController();
                orderController.setAnchor_main_rightPanel(anchor_main_rightPanel);
                orderController.setCurrentEmployee(currentEmployee);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/order.png");

        orderController.reloadOrder();
        orderController.resetFilterIconClicked();

        resetSubmenu();

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
    void supplierButtonOnClick() {
        if (supplierView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/supplier/supplier.fxml"));
                supplierView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/supplier.png");

        resetSubmenu();

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
    void categoryButtonOnClick() {
        if (categoryView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/category/category.fxml"));
                categoryView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/category.png");

        resetSubmenu();

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
    void hideReportSubMenu() {
        icon_reportDown.setVisible(true);
        icon_reportRight.setVisible(false);
        vbox_report_subMenu.setVisible(false);
    }

    @FXML
    void reportButtonOnClick() {
        icon_reportDown.setVisible(false);
        icon_reportRight.setVisible(true);
        vbox_report_subMenu.setVisible(true);
    }

    @FXML
    void hideReportSubMenuHR() {
        icon_reportDownHR.setVisible(true);
        icon_reportRightHR.setVisible(false);
        vbox_report_subMenuHR.setVisible(false);
    }
    @FXML
    void reportButtonOnClickHR() {
        icon_reportDownHR.setVisible(false);
        icon_reportRightHR.setVisible(true);
        vbox_report_subMenuHR.setVisible(true);
    }

    @FXML
    void reportProductQuantityButtonOnClick() {
        icon_reportRight.setVisible(true);
        icon_reportDown.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRight.setFill(Paint.valueOf("#4A55A2"));

        icon_reportRightHR.setVisible(true);
        icon_reportDownHR.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRightHR.setFill(Paint.valueOf("#4A55A2"));

        btn_subMenu_productQuantity.setId("button-custom-submenu-selected");
        btn_subMenu_productRevenue.setId("button-custom-submenu");
//        btn_subMenu_employee.setId("button-custom-submenu");
        btn_subMenu_order.setId("button-custom-submenu");

        btn_subMenu_productQuantityHR.setId("button-custom-submenu-selected");
        btn_subMenu_productRevenueHR.setId("button-custom-submenu");
        btn_subMenu_employeeHR.setId("button-custom-submenu");
        btn_subMenu_orderHR.setId("button-custom-submenu");

        if (reportProductQuantityView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/report/report-product-quantity.fxml"));
                reportProductQuantityView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/report.png");

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(reportProductQuantityView);
    }

    @FXML
    void reportProductRevenueButtonOnClick() {
        icon_reportRight.setVisible(true);
        icon_reportDown.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRight.setFill(Paint.valueOf("#4A55A2"));

        icon_reportRightHR.setVisible(true);
        icon_reportDownHR.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRightHR.setFill(Paint.valueOf("#4A55A2"));

        btn_subMenu_productQuantity.setId("button-custom-submenu");
        btn_subMenu_productRevenue.setId("button-custom-submenu-selected");
//        btn_subMenu_employee.setId("button-custom-submenu");
        btn_subMenu_order.setId("button-custom-submenu");

        btn_subMenu_productQuantityHR.setId("button-custom-submenu");
        btn_subMenu_productRevenueHR.setId("button-custom-submenu-selected");
        btn_subMenu_employeeHR.setId("button-custom-submenu");
        btn_subMenu_orderHR.setId("button-custom-submenu");

        if (reportProductRevenueView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/report/report-product-revenue.fxml"));
                reportProductRevenueView = fxmlLoader.load();
                productRevenueController = fxmlLoader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/report.png");

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(reportProductRevenueView);

        productRevenueController.viewButtonOnClick(); // reset data
    }

    @FXML
    void reportOrderButtonOnClick() {
        icon_reportRight.setVisible(true);
        icon_reportDown.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRight.setFill(Paint.valueOf("#4A55A2"));

        icon_reportRightHR.setVisible(true);
        icon_reportDownHR.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRightHR.setFill(Paint.valueOf("#4A55A2"));

        btn_subMenu_productQuantity.setId("button-custom-submenu");
        btn_subMenu_productRevenue.setId("button-custom-submenu");
//        btn_subMenu_employee.setId("button-custom-submenu");
        btn_subMenu_order.setId("button-custom-submenu-selected");

        btn_subMenu_productQuantityHR.setId("button-custom-submenu");
        btn_subMenu_productRevenueHR.setId("button-custom-submenu");
        btn_subMenu_employeeHR.setId("button-custom-submenu");
        btn_subMenu_orderHR.setId("button-custom-submenu-selected");

        if (reportOrderView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/report/report-order-statistics.fxml"));
                reportOrderView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/report.png");

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel-selected");

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(reportOrderView);
    }

    @FXML
    void reportEmployeeButtonOnClick() {
        icon_reportRight.setVisible(true);
        icon_reportDown.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRight.setFill(Paint.valueOf("#4A55A2"));

        icon_reportRightHR.setVisible(true);
        icon_reportDownHR.setFill(Paint.valueOf("#4A55A2"));
        icon_reportRightHR.setFill(Paint.valueOf("#4A55A2"));

        btn_subMenu_productQuantity.setId("button-custom-submenu");
        btn_subMenu_productRevenue.setId("button-custom-submenu");
//        btn_subMenu_employee.setId("button-custom-submenu-selected");
        btn_subMenu_order.setId("button-custom-submenu");

        btn_subMenu_productQuantityHR.setId("button-custom-submenu");
        btn_subMenu_productRevenueHR.setId("button-custom-submenu");
        btn_subMenu_employeeHR.setId("button-custom-submenu-selected");
        btn_subMenu_orderHR.setId("button-custom-submenu");

        if (reportEmployeeView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml_1920/report/report-employee.fxml"));
                reportEmployeeView = fxmlLoader.load();
                employeeSalaryController = fxmlLoader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        setMenuImage("images/report.png");

        btn_leftPanel_dashboard.setId("button-custom-mainPanel");
        btn_leftPanel_productList.setId("button-custom-mainPanel");
        btn_leftPanel_employeeList.setId("button-custom-mainPanel");
        btn_leftPanel_customerList.setId("button-custom-mainPanel");
        btn_leftPanel_orderList.setId("button-custom-mainPanel");
        btn_leftPanel_supplierList.setId("button-custom-mainPanel");
        btn_leftPanel_categoryList.setId("button-custom-mainPanel");
        btn_leftPanel_reportList.setId("button-custom-mainPanel-selected");
        btn_leftPanel_reportListHR.setId("button-custom-mainPanel-selected");

        employeeSalaryController.reloadEmployee();

        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(reportEmployeeView);

    }

    public void isSales() {
        vbox_sales.setVisible(true);
        vbox_HR.setVisible(false);
    }

    public void isHR() {
        vbox_sales.setVisible(false);
        vbox_HR.setVisible(true);
    }

    private void setMenuImage(String imageUrl) {
        Image image = null;
        URL resourceURL = ComponentManagementApplication.class.getResource(imageUrl);

        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            image = new Image(resourcePath);
        }

        leftPanel_imageView.setImage(image);
    }

    private void resetSubmenu() {
        icon_reportDown.setFill(Paint.valueOf("#fff"));
        btn_subMenu_productQuantity.setId("button-custom-submenu");
        btn_subMenu_productRevenue.setId("button-custom-submenu");
//        btn_subMenu_employee.setId("button-custom-submenu");
        btn_subMenu_order.setId("button-custom-submenu");

        icon_reportDownHR.setFill(Paint.valueOf("#fff"));
        btn_subMenu_productQuantityHR.setId("button-custom-submenu");
        btn_subMenu_productRevenueHR.setId("button-custom-submenu");
        btn_subMenu_employeeHR.setId("button-custom-submenu");
        btn_subMenu_orderHR.setId("button-custom-submenu");
    }


}
