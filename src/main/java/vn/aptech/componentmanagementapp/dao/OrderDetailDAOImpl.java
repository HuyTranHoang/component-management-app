package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.OrderDetail;
import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailDAOImpl implements OrderDetailDAO{
    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public OrderDetail getById(long orderDetailId) {
        OrderDetail orderDetail = null;

        String query = "SELECT * FROM order_detail WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderDetailId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    orderDetail = new OrderDetail();
                    setOrderDetail(orderDetail, resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderDetail;
    }

    @Override
    public List<OrderDetail> getAll() {
        String query = "SELECT * FROM order_detail ORDER BY id";
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                OrderDetail orderDetail = new OrderDetail();
                setOrderDetail(orderDetail, resultSet);
                orderDetails.add(orderDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    @Override
    public void add(OrderDetail orderDetail) {
        String query = "INSERT INTO order_detail (name, price, quantity, discount, total_amount, order_id, product_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(orderDetail, statement);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    orderDetail.setId(generatedId);
                } else {
                    throw new SQLException("Adding order detail failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(OrderDetail orderDetail) {
        String query = "UPDATE order_detail SET name = ?, price = ?, quantity = ?, discount = ?, " +
                "total_amount = ?, order_id = ?, product_id = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(orderDetail, statement);
            statement.setLong(8, orderDetail.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long orderDetailId) {
        String query = "DELETE FROM order_detail WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderDetailId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(OrderDetail orderDetail, PreparedStatement statement) throws SQLException {
        statement.setString(1, orderDetail.getName());
        statement.setDouble(2, orderDetail.getPrice());
        statement.setInt(3, orderDetail.getQuantity());
        statement.setInt(4, orderDetail.getDiscount());
        statement.setDouble(5, orderDetail.getTotalAmount());
        statement.setLong(6, orderDetail.getOrderId());
        statement.setLong(7, orderDetail.getProductId());
    }

    private void setOrderDetail(OrderDetail orderDetail, ResultSet resultSet) throws SQLException {
        orderDetail.setId(resultSet.getLong("id"));
        orderDetail.setName(resultSet.getString("name"));
        orderDetail.setPrice(resultSet.getDouble("price"));
        orderDetail.setQuantity(resultSet.getInt("quantity"));
        orderDetail.setDiscount(resultSet.getInt("discount"));
        orderDetail.setTotalAmount(resultSet.getDouble("total_amount"));
        orderDetail.setOrderId(resultSet.getLong("order_id"));
        orderDetail.setProductId(resultSet.getLong("product_id"));
    }

    @Override
    public List<OrderDetail> getAllByOrderId(long orderId) {
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();

        String query = "SELECT * FROM order_detail WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderDetail orderDetail = new OrderDetail();
                    setOrderDetail(orderDetail, resultSet);
                    orderDetails.add(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderDetails;
    }

    @Override
    public Map<String, Double> getTotalAmountByCategory(LocalDate fromDate, LocalDate toDate) {
        Map<String, Double> totalAmountMap = new HashMap<>();

        String query = "SELECT c.name AS category_name, SUM(od.total_amount) AS total_amount_sold " +
                "FROM order_detail od " +
                "JOIN products p ON od.product_id = p.id " +
                "JOIN categories c ON p.category_id = c.id " +
                "JOIN orders o on o.id = od.order_id " +
                "WHERE o.order_date BETWEEN ? AND ? " +
                "GROUP BY c.name;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryName = resultSet.getString("category_name");
                    double totalAmount = resultSet.getDouble("total_amount_sold");

                    totalAmountMap.put(categoryName, totalAmount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalAmountMap;
    }

    @Override
    public Map<String, Integer> getTotalQuantityByCategory(LocalDate fromDate, LocalDate toDate) {
        Map<String, Integer> totalQuantityMap = new HashMap<>();

        String query = "SELECT c.name AS category_name, SUM(od.quantity) AS total_quantity_sold " +
                "FROM order_detail od " +
                "JOIN products p ON od.product_id = p.id " +
                "JOIN categories c ON p.category_id = c.id " +
                "JOIN orders o on o.id = od.order_id " +
                "WHERE o.order_date BETWEEN ? AND ? " +
                "GROUP BY c.name;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String categoryName = resultSet.getString("category_name");
                    int quantity = resultSet.getInt("total_quantity_sold");

                    totalQuantityMap.put(categoryName, quantity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalQuantityMap;
    }
}
