package vn.aptech.componentmanagementapp.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagementController implements Initializable {
    @FXML
    private AnchorPane anchor_main_rightPanel;

    @FXML
    private MFXButton btn_leftPanel_Exit;

    @FXML
    private MFXButton btn_leftPanel_OrderList;

    @FXML
    private MFXButton btn_leftPanel_customerList;

    @FXML
    private MFXButton btn_leftPanel_productList;

    //    Variable
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("main-product.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            anchor_main_rightPanel.getChildren().add(anchorPane);
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
    void productListButtonOnClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("main-product.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            btn_leftPanel_productList.setId("button-custom-selected");
            btn_leftPanel_customerList.setId("button-custom");
            btn_leftPanel_OrderList.setId("button-custom");

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(anchorPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void customerListButtonOnClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("main-customer.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();

            btn_leftPanel_productList.setId("button-custom");
            btn_leftPanel_customerList.setId("button-custom-selected");
            btn_leftPanel_OrderList.setId("button-custom");

            anchor_main_rightPanel.getChildren().clear();
            anchor_main_rightPanel.getChildren().add(anchorPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void orderListButtonOnClick() {
        System.out.println("789");
    }
}
