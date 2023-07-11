package vn.aptech.componentmanagementapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;

import java.io.IOException;

public class ProductAddController {

    // Cached views
    private AnchorPane productView;

    private AnchorPane anchor_main_rightPanel; // Truyền từ Product controller vào

    public void setProductView(AnchorPane productView) {
        this.productView = productView;
    }

    public void setAnchor_main_rightPanel(AnchorPane anchor_main_rightPanel) {
        this.anchor_main_rightPanel = anchor_main_rightPanel;
    }

    @FXML
    void listProductButtonOnClick() {
        if (productView == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("fxml/product/main-product.fxml"));
                productView = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        anchor_main_rightPanel.getChildren().clear();
        anchor_main_rightPanel.getChildren().add(productView);
    }

}
