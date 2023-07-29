package vn.aptech.componentmanagementapp.util;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import vn.aptech.componentmanagementapp.ComponentManagementApplication;

import java.net.URL;

public class SetImageAlert {

    public static final String CONFIRMATION = "images/alert/confirmation.png";
    public static final String ERROR = "images/alert/error.png";
    public static final String SUCCESS = "images/alert/success.png";
    public static final String WARNING = "images/alert/warning.png";

    public static void setIconAlert(Alert confirmation, String url) {
        URL resourceURL = ComponentManagementApplication.class.getResource(url);
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            ImageView image = new ImageView(resourcePath);
            confirmation.setGraphic(image);
            image.setFitHeight(50);
            image.setFitWidth(50);
        }

        resourceURL = ComponentManagementApplication.class.getResource("images/product.png");
        if (resourceURL != null) {
            String resourcePath = resourceURL.toExternalForm();
            Image iconImage = new Image(resourcePath);
            Stage stage = (Stage) confirmation.getDialogPane().getScene().getWindow();
            stage.getIcons().add(iconImage);
        }
    }
}
