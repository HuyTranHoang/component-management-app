package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.DepartmentDAO;
import vn.aptech.componentmanagementapp.dao.DepartmentDAOImpl;
import vn.aptech.componentmanagementapp.model.Department;

import java.util.List;

public class DepartmentService {
    private DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAOImpl();
    }

    public List<Department> getAllDepartment() {
        return departmentDAO.getAll();
    }
    public Department getDepartmentById(int departmentId) {
        return departmentDAO.getById(departmentId);
    }

    public void addDepartment(Department department) {

        departmentDAO.add(department);
    }

    public void updateDepartment(Department department) {

        departmentDAO.update(department);
    }

    public void deleteDepartment(int departmentId) {

        departmentDAO.delete(departmentId);
    }
}

