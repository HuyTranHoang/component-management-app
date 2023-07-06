package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public List<Customer> getAll() {
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<Customer> list = new ArrayList<>();
        Customer customer;

        try {
            String query = "SELECT * FROM customers";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setName(resultSet.getString("name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setPhone(resultSet.getString("phone"));
                customer.setEmail(resultSet.getString("email"));
                list.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
        }

        return list;
    }

    @Override
    public Customer getById(int customerId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Customer customer = null;

        try {
            String query = "SELECT * FROM customers WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, customerId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setName(resultSet.getString("name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setPhone(resultSet.getString("phone"));
                customer.setEmail(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
        }

        return customer;
    }

    @Override
    public void add(Customer customer) {
        PreparedStatement statement = null;
        try {
            String query = "INSERT INTO customers (name, address, phone, email) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getEmail());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(statement);
        }

    }

    @Override
    public void update(Customer customer) {
        PreparedStatement statement = null;
        try {
            String query = "UPDATE customers SET name = ?, address = ?, phone = ?, email = ? WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getEmail());
            statement.setLong(5, customer.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(statement);
        }
    }

    @Override
    public void delete(int customerId) {
        PreparedStatement statement = null;
        try {
            String query = "DELETE FROM customers WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, customerId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(statement);
        }

    }
}
