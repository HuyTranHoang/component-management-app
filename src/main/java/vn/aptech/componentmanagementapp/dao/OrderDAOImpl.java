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
        String query = "SELECT * FROM orders ORDER BY id";
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
                "total_amount, note, is_cancelled, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                "total_amount, note, is_cancelled, customer_id, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                " FROM orders WHERE order_date >= ? AND order_date < ? AND is_cancelled = FALSE" +
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
    public int weeklyNewOrder() {
        int count = 0;
        String query = "SELECT COUNT(*) AS new_orders_count FROM orders" +
                " WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)" +
                " AND created_at < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week' AND is_cancelled = FALSE";
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
    public double todayTotalAmount() {
        double totalAmount = 0;
        String query = "SELECT SUM(total_amount) AS total_amount_today FROM orders " +
                "WHERE DATE(order_date) = CURRENT_DATE AND is_cancelled = FALSE";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                totalAmount = rs.getDouble("total_amount_today");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalAmount;
    }

    @Override
    public double yesterdayTotalAmount() {
        double totalAmount = 0;
        String query = "SELECT SUM(total_amount) AS total_amount_yesterday FROM orders " +
                "WHERE DATE(order_date) = CURRENT_DATE - INTERVAL '1 day' AND is_cancelled = FALSE";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                totalAmount = rs.getDouble("total_amount_yesterday");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalAmount;
    }

    @Override
    public void cancelOrder(long orderId) {
        String query = "UPDATE orders SET is_cancelled = true WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int countOrder(LocalDate fromDate, LocalDate toDate) {
        int countOrder = 0;
        String query = "SELECT COUNT(*) AS total_orders FROM orders " +
                "WHERE order_date >= ? AND order_date < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    countOrder = resultSet.getInt("total_orders");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  countOrder;
    }

    @Override
    public double sumTotalAmount(LocalDate fromDate, LocalDate toDate) {
        double sumTotalAmount = 0;
        String query = "SELECT SUM(total_amount) AS total_amount_sum FROM orders " +
                "WHERE order_date >= ? AND order_date < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    sumTotalAmount = resultSet.getDouble("total_amount_sum");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sumTotalAmount;
    }

    @Override
    public int countCustomer(LocalDate fromDate, LocalDate toDate) {
        int countCustomer = 0;
        String query = "SELECT COUNT(DISTINCT customer_id) AS total_unique_customers FROM orders " +
                "WHERE order_date >= ? AND order_date < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    countCustomer = resultSet.getInt("total_unique_customers");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  countCustomer;
    }

    @Override
    public int countCanceledOrder(LocalDate fromDate, LocalDate toDate) {
        int countCanceledOrder = 0;
        String query = "SELECT COUNT(*) AS total_cancelled_orders FROM orders " +
                "WHERE is_cancelled = true AND order_date >= ? AND order_date < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    countCanceledOrder = resultSet.getInt("total_cancelled_orders");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  countCanceledOrder;
    }

    @Override
    public void update(Order order) {
        String query = "UPDATE orders SET order_date = ?, delivery_date = ?, receive_date = ?, delivery_location = ?, total_amount = ?, note = ?, " +
                "is_cancelled = ?, customer_id = ?, employee_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(order, statement);
            statement.setLong(10, order.getId());
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
        statement.setBoolean(7, order.isCancelled());
        statement.setLong(8, order.getCustomerId());
        statement.setLong(9, order.getEmployeeId());
    }

    private void setOrder(Order order, ResultSet resultSet) throws SQLException {
        order.setId(resultSet.getLong("id"));
        order.setOrderDate(resultSet.getTimestamp("order_date").toLocalDateTime());
        order.setDeliveryDate(resultSet.getTimestamp("delivery_date").toLocalDateTime());
        order.setReceiveDate(resultSet.getTimestamp("receive_date").toLocalDateTime());
        order.setDeliveryLocation(resultSet.getString("delivery_location"));
        order.setTotalAmount(resultSet.getDouble("total_amount"));
        order.setNote(resultSet.getString("note"));
        order.setCancelled(resultSet.getBoolean("is_cancelled"));
        order.setCustomerId(resultSet.getLong("customer_id"));
        order.setEmployeeId(resultSet.getLong("employee_id"));
    }
}
