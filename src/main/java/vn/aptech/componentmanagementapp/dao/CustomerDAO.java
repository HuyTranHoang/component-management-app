package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;

import java.util.ArrayList;
import java.util.List;

public interface CustomerDAO extends BaseDAO<Customer>{

    // Các phương thức cụ thể cho CustomerDAO ( Sort, search .. )
    Customer getById(long customerId);
    List<Customer> getAll();
}
