package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.CustomerDAO;
import vn.aptech.componentmanagementapp.dao.CustomerDAOImpl;
import vn.aptech.componentmanagementapp.model.Customer;

import java.util.ArrayList;

public class CustomerService {
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }

    public ArrayList<Customer> getAllCustomer() {
        return customerDAO.getAllCustomer();
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    public void addCustomer(Customer customer) {
        customerDAO.addCustomer(customer);
    }

    public void updateCustomer(Customer customer) {
        customerDAO.updateCustomer(customer);
    }

    public void deleteCustomer(int customerId) {
        customerDAO.deleteCustomer(customerId);
    }
}
