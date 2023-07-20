package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    setOrder(order, resultSet);
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
                setOrder(order, resultSet);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    @Override
    public void add(Order order) {
        String query = "INSERT INTO orders(order_date, delivery_date, receive_date, delivery_location, " +
                "total_amount, note, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(order, statement);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    order.setId(generatedId);
                } else {
                    throw new SQLException("Adding order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long addReturnId(Order order) {
        String query = "INSERT INTO orders(order_date, delivery_date, receive_date, delivery_location, " +
                "total_amount, note, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(order, statement);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    order.setId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Adding customer failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<LocalDate, Double> weeklyTotalAmounts(LocalDate startOfWeek, LocalDate endOfWeek) {
        Map<LocalDate, Double> list = new HashMap<>();
        String query = "SELECT DATE(order_date) AS order_date, SUM(total_amount) AS total_amount_per_day" +
                " FROM orders WHERE order_date >= ? AND order_date < ?" +
                " GROUP BY DATE(order_date) ORDER BY order_date";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(startOfWeek));
            statement.setDate(2, Date.valueOf(endOfWeek));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.put(
                            resultSet.getDate("order_date").toLocalDate(),
                            resultSet.getDouble("total_amount_per_day")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int getWeeklyNewOrder() {
        int count = 0;
        String query = "SELECT COUNT(*) AS new_orders_count FROM orders" +
                " WHERE order_date >= DATE_TRUNC('week', CURRENT_DATE)" +
                " AND order_date < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week';";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                count = rs.getInt("new_orders_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void update(Order order) {
        String query = "UPDATE orders SET order_date = ?, delivery_date = ?, receive_date = ?, delivery_location = ?, total_amount = ?, note = ?, " +
                "customer_id = ?, employee_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(order, statement);
            statement.setLong(9, order.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void statementInsertUpdate(Order order, PreparedStatement statement) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(order.getOrderDate()));
        statement.setTimestamp(2, Timestamp.valueOf(order.getDeliveryDate()));
        statement.setTimestamp(3, Timestamp.valueOf(order.getReceiveDate()));
        statement.setString(4, order.getDeliveryLocation());
        statement.setDouble(5, order.getTotalAmount());
        statement.setString(6, order.getNote());
        statement.setLong(7, order.getCustomerId());
        statement.setLong(8, order.getEmployeeId());
    }

    private void setOrder(Order order, ResultSet resultSet) throws SQLException {
        order.setId(resultSet.getLong("id"));
        order.setOrderDate(resultSet.getTimestamp("order_date").toLocalDateTime());
        order.setDeliveryDate(resultSet.getTimestamp("delivery_date").toLocalDateTime());
        order.setReceiveDate(resultSet.getTimestamp("receive_date").toLocalDateTime());
        order.setDeliveryLocation(resultSet.getString("delivery_location"));
        order.setTotalAmount(resultSet.getDouble("total_amount"));
        order.setNote(resultSet.getString("note"));
        order.setCustomerId(resultSet.getLong("customer_id"));
        order.setEmployeeId(resultSet.getLong("employee_id"));
    }
}
