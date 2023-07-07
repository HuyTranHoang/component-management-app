package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.*;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Employee getById(long employeeId) {
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
        String query = "SELECT id, email, password, citizen_identification, department_id FROM employees";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setId(resultSet.getString("id"));
                loginInfo.setEmail(resultSet.getString("email"));
                loginInfo.setPassword(resultSet.getString("password"));
                loginInfo.setCitizen_id(resultSet.getString("citizen_identification"));
                loginInfo.setDepartment_id(resultSet.getLong("department_id"));
                loginInfos.add(loginInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loginInfos;
    }

    @Override
    public void updatePassword(long id, String password) {
        String query = "UPDATE employees SET password = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, password);
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
