package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;

import java.util.ArrayList;

public interface CustomerDAO {
    ArrayList<Customer> getAllCustomer();
    Customer getCustomerById(int customerId);
    void addCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(int customerId);
}
