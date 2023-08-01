package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.LoginInfo;

import java.util.List;

public interface EmployeeDAO extends BaseDAO<Employee> {
    List<LoginInfo> getAllLoginInfo();
    void updatePassword(long id, String password);
}
