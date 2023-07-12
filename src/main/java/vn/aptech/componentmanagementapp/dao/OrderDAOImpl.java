package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.model.Employee;
import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Order getById(long orderId) {
        Order order = null;

        String query = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    order = new Order();
                    order.setId(resultSet.getLong("id"));
                    order.setOrderDate(resultSet.getTimestamp("order_date").toLocalDateTime());
                    order.setDeliveryDate(resultSet.getTimestamp("delivery_date").toLocalDateTime());
                    order.setShipmentDate(resultSet.getTimestamp("shipment_date").toLocalDateTime());
                    order.setDeliveryLocation(resultSet.getString("delivery_location"));
                    order.setTotalAmount(resultSet.getDouble("total_amount"));
                    order.setNote(resultSet.getString("note"));
                    order.setCustomerId(resultSet.getLong("customer_id"));
                    order.setEmployeeId(resultSet.getLong("employee_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    @Override
    public List<Order> getAll() {
        String query = "SELECT * FROM orders";
        ArrayList<Order> orders = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getLong("id"));
                order.setOrderDate(resultSet.getTimestamp("order_date").toLocalDateTime());
                order.setDeliveryDate(resultSet.getTimestamp("delivery_date").toLocalDateTime());
                order.setShipmentDate(resultSet.getTimestamp("shipment_date").toLocalDateTime());
                order.setDeliveryLocation(resultSet.getString("delivery_location"));
                order.setTotalAmount(resultSet.getDouble("total_amount"));
                order.setNote(resultSet.getString("note"));
                order.setCustomerId(resultSet.getLong("customer_id"));
                order.setEmployeeId(resultSet.getLong("employee_id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    @Override
    public void add(Order order) {
        String query = "INSERT INTO orders(order_date, delivery_date, shipment_date, delivery_location, " +
                "total_amount, note, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(order, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Order order) {
        String query = "UPDATE orders SET order_date = ?, delivery_date = ?, shipment_date = ?, delivery_location = ?, total_amount = ?, note = ?, " +
                "customer_id = ?, employee_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(order, statement);
            statement.setLong(9, order.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Order order, PreparedStatement statement) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(order.getOrderDate()));
        statement.setTimestamp(2, Timestamp.valueOf(order.getDeliveryDate()));
        statement.setTimestamp(3, Timestamp.valueOf(order.getShipmentDate()));
        statement.setString(4, order.getDeliveryLocation());
        statement.setDouble(5, order.getTotalAmount());
        statement.setString(6, order.getNote());
        statement.setLong(7, order.getCustomerId());
        statement.setLong(8, order.getEmployeeId());
    }


    @Override
    public void delete(long orderId) {
        String query = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
