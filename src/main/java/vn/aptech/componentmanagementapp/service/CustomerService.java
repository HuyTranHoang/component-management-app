package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.CustomerDAO;
import vn.aptech.componentmanagementapp.dao.CustomerDAOImpl;
import vn.aptech.componentmanagementapp.model.Customer;

import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }

    public List<Customer> getAllCustomer() {
        return customerDAO.getAll();
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.getById(customerId);
    }

    public void addCustomer(Customer customer) {
        customerDAO.add(customer);
    }

    public void updateCustomer(Customer customer) {
        customerDAO.update(customer);
    }

    public void deleteCustomer(int customerId) {
        customerDAO.delete(customerId);
    }
}
