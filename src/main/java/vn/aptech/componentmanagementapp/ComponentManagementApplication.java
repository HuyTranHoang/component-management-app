package vn.aptech.componentmanagementapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ComponentManagementApplication extends Application {

    private double x;
    private double y;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ComponentManagementApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ComponentManagementController controller = fxmlLoader.getController();
        controller.setStage(stage);

        scene.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(.9);
        });

        scene.setOnMouseReleased(event -> stage.setOpacity(1));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        stage.setTitle("Component Management Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}