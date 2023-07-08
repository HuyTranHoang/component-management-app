package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Department;

import java.util.List;

public interface DepartmentDAO extends BaseDAO<Department> {
    Department getById(long departmentId);
    List<Department> getAll();
}
