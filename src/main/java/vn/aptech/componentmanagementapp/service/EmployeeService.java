package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.EmployeeDAO;
import vn.aptech.componentmanagementapp.dao.EmployeeDAOImpl;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.LoginInfo;

import java.util.List;

public class EmployeeService {

    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    public List<Employee> getAllEmployee() {
        return employeeDAO.getAll();
    }

    public List<LoginInfo> getAllLoginInfo() {
        return employeeDAO.getAllLoginInfo();
    }

    public void updateEmployeePassword(long id, String password) {
        employeeDAO.updatePassword(id, password);
    }
    public Employee getEmployeeById(long employeeId) {
        return employeeDAO.getById(employeeId);
    }

    public void addEmployee(Employee employee) {

        employeeDAO.add(employee);
    }

    public void updateEmployee(Employee employee) {

        employeeDAO.update(employee);
    }

    public void deleteEmployee(int employeeId) {

        employeeDAO.delete(employeeId);
    }
}
