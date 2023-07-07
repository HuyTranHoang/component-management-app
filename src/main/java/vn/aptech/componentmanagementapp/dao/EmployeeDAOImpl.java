package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.LoginInfo;
import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Employee getById(long id) {
        return null;
    }

    @Override
    public List<Employee> getAll() {
        String query = "SELECT * FROM employees";

        ArrayList<Employee> employees = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getInt("id"));
                employee.setName(resultSet.getString("name"));
                employee.setAddress(resultSet.getString("address"));
                employee.setPhone(resultSet.getString("phone"));
                employee.setEmail(resultSet.getString("email"));
                employee.setSalary(resultSet.getDouble("salary"));
                employee.setImage(resultSet.getString("image"));
                employee.setCitizenID(resultSet.getString("citizen_identification"));
                employee.setDateOfBirth(resultSet.getDate("date_of_birth").toLocalDate());
                employee.setDateOfHire(resultSet.getDate("date_of_hire").toLocalDate());
                employee.setDepartmentId(resultSet.getLong("department_id"));
                employee.setPositionId(resultSet.getLong("position_id"));

                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public void add(Employee entity) {

    }

    @Override
    public void update(Employee entity) {

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<LoginInfo> getAllLoginInfo() {
        List<LoginInfo> loginInfos = new ArrayList<>();
        String query = "SELECT email, password FROM employees";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setEmail(resultSet.getString("email"));
                loginInfo.setPassword(resultSet.getString("password"));
                loginInfos.add(loginInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loginInfos;
    }
}
