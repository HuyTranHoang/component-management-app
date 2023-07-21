package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;


public interface CustomerDAO extends BaseDAO<Customer>{
    int getWeeklyNewCustomer();
}
