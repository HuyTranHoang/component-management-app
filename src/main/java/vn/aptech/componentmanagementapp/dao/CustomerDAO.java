package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;

import java.time.LocalDate;


public interface CustomerDAO extends BaseDAO<Customer>{
    int getWeeklyNewCustomer();
}
