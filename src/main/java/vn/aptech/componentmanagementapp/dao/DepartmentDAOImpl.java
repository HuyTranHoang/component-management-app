package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public Department getById(long departmentId) {
        Department department = null;

        String query = "SELECT * FROM departments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    department = new Department();
                    department.setId(resultSet.getLong("id"));
                    department.setName(resultSet.getString("name"));
                    department.setDescription(resultSet.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return department;
    }

    @Override
    public List<Department> getAll() {
        String query = "SELECT * FROM departments";
        ArrayList<Department> departments = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Department department = new Department();
                department.setId(resultSet.getInt("id"));
                department.setName(resultSet.getString("name"));
                department.setDescription(resultSet.getString("description"));
                departments.add(department);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }

    @Override
    public void add(Department department) {
        String query = "INSERT INTO departments (id,name,description) VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(department, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Department department) {
        String query = "UPDATE departments SET id = ?, name = ?, description = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(department, statement);
            statement.setLong(4,department.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Department department, PreparedStatement statement) throws SQLException {
        statement.setLong(1,department.getId());
        statement.setString(2, department.getName());
        statement.setString(3, department.getDescription());
    }

    @Override
    public void delete(long departmentId) {
        String query = "DELETE FROM departments WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, departmentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
