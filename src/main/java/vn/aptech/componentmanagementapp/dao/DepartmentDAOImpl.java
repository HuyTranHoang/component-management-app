package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.model.Department;
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
                    department.setId(resultSet.getInt("id"));
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
    public void add(Department entity) {

    }

    @Override
    public void update(Department entity) {

    }

    @Override
    public void delete(long id) {

    }
}
