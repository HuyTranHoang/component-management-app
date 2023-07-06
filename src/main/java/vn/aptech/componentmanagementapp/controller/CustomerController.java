package vn.aptech.componentmanagementapp.controller;
import javafx.fxml.Initializable;
import vn.aptech.componentmanagementapp.service.CustomerService;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private CustomerService customerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.customerService = new CustomerService();
    }
}
