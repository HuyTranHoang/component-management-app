package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Customer;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public Customer getById(long customerId) {
        Customer customer = null;

        String query = "SELECT * FROM customers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    customer = new Customer();
                    setCustomer(customer, resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    @Override
    public List<Customer> getAll() {
        String query = "SELECT * FROM customers";
        ArrayList<Customer> customers = new ArrayList<>();

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Customer customer = new Customer();
                setCustomer(customer, resultSet);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    @Override
    public void add(Customer customer) {
        String query = "INSERT INTO customers (name, address, phone, email) VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(customer, statement);
            if (customer.getEmail().isEmpty()) {
                statement.setNull(4, Types.VARCHAR);
            }
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    customer.setId(generatedId);
                } else {
                    throw new SQLException("Adding customer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Customer customer) {
        String query = "UPDATE customers SET name = ?, address = ?, phone = ?, email = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(customer, statement);
            statement.setLong(5, customer.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long customerId) {
        String query = "DELETE FROM customers WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, customerId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Customer customer, PreparedStatement statement) throws SQLException {
        statement.setString(1, customer.getName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getPhone());
        statement.setString(4, customer.getEmail());
    }

    private void setCustomer(Customer customer, ResultSet resultSet) throws SQLException {
        customer.setId(resultSet.getLong("id"));
        customer.setName(resultSet.getString("name"));
        customer.setAddress(resultSet.getString("address"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setEmail(resultSet.getString("email"));
    }

    @Override
    public int getWeeklyNewCustomer() {
        int count = 0;

        String query = "SELECT COUNT(*) AS new_customers_count FROM customers" +
                " WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)" +
                " AND created_at < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week';";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                count = rs.getInt("new_customers_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
