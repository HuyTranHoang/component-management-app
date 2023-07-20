module vn.aptech.componentmanagementapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    requires MaterialFX;

    requires net.synedra.validatorfx;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires AnimateFX;

    opens vn.aptech.componentmanagementapp to javafx.fxml;
    exports vn.aptech.componentmanagementapp;
    opens vn.aptech.componentmanagementapp.controller to javafx.fxml;
    exports vn.aptech.componentmanagementapp.controller;

    // Open the package to javafx.base
    exports vn.aptech.componentmanagementapp.model;
    opens vn.aptech.componentmanagementapp.model to javafx.base;

    exports vn.aptech.componentmanagementapp.controller.dashboard;
    opens vn.aptech.componentmanagementapp.controller.dashboard to javafx.fxml;

    exports vn.aptech.componentmanagementapp.controller.product;
    opens vn.aptech.componentmanagementapp.controller.product to javafx.fxml;

    exports vn.aptech.componentmanagementapp.controller.employee;
    opens vn.aptech.componentmanagementapp.controller.employee to javafx.fxml;

    exports vn.aptech.componentmanagementapp.controller.customer;
    opens vn.aptech.componentmanagementapp.controller.customer to javafx.fxml;

    opens vn.aptech.componentmanagementapp.controller.order to javafx.fxml;
    exports vn.aptech.componentmanagementapp.controller.order;

    exports vn.aptech.componentmanagementapp.controller.orderdetail;
    opens vn.aptech.componentmanagementapp.controller.orderdetail to javafx.fxml;

    exports vn.aptech.componentmanagementapp.controller.category;
    opens vn.aptech.componentmanagementapp.controller.category to javafx.fxml;

    exports vn.aptech.componentmanagementapp.controller.supplier;
    opens vn.aptech.componentmanagementapp.controller.supplier to javafx.fxml;

}
