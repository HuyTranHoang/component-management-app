module vn.aptech.componentmanagementapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires MaterialFX;

    requires net.synedra.validatorfx;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens vn.aptech.componentmanagementapp to javafx.fxml;
    exports vn.aptech.componentmanagementapp;
    opens vn.aptech.componentmanagementapp.controller to javafx.fxml;
    exports vn.aptech.componentmanagementapp.controller;
}