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
    public void add(Employee employee) {
        String query = "INSERT INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, employee.getId());
            statement.setString(2,employee.getName());
            statement.setString(3, employee.getAddress());
            statement.setString(4, employee.getPhone());
            statement.setString(5, employee.getEmail());
            statement.setDouble(6,employee.getSalary());
            statement.setString(7,employee.getImage());
            statement.setString(8, employee.getCitizenID());
            statement.setObject(9,employee.getDateOfBirth());
            statement.setObject(10,employee.getDateOfHire());
            statement.setLong(11,employee.getDepartmentId());
            statement.setLong(12,employee.getPositionId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Employee employee) {
        String query = "UPDATE employees SET name = ?, address = ?, phone = ?, email = ?, salary = ?, image = ?, citizen_identification = ?, date_of_birth = ?, date_of_hire = ?, department_id = ?, position_id = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,employee.getName());
            statement.setString(2, employee.getAddress());
            statement.setString(3, employee.getPhone());
            statement.setString(4, employee.getEmail());
            statement.setDouble(5,employee.getSalary());
            statement.setString(6,employee.getImage());
            statement.setString(7, employee.getCitizenID());
            statement.setObject(8,employee.getDateOfBirth());
            statement.setObject(9,employee.getDateOfHire());
            statement.setLong(10,employee.getDepartmentId());
            statement.setLong(11,employee.getPositionId());
            statement.setLong(12, employee.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(long employeeId) {
        String query = "DELETE FROM employees WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,employeeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
