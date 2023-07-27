package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAOImpl implements ProductDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Product getById(long productId) {
        Product product = null;

        String query = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    product = new Product();
                    setProduct(product, resultSet);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products ORDER BY id";
        ArrayList<Product> products = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Product product = new Product();
                setProduct(product, resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    @Override
    public void add(Product product) {
        String query = "INSERT INTO products(product_code, name, price, stock_quantity, month_of_warranty, note, " +
                "supplier_id, category_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(product, statement);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    product.setId(generatedId); // Set the generated ID on the product object
                } else {
                    throw new SQLException("Adding product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product product) {
        String query = "UPDATE products SET product_code = ?, name = ?, price = ?, stock_quantity = ?, month_of_warranty = ?, note = ?, " +
                "supplier_id = ?, category_id = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(product, statement);
            statement.setLong(9,product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long productId) {
        String query = "DELETE FROM products WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Product product, PreparedStatement statement) throws SQLException {
        statement.setString(1,product.getProductCode());
        statement.setString(2,product.getName());
        statement.setDouble(3,product.getPrice());
        statement.setInt(4,product.getStockQuantity());
        statement.setInt(5,product.getMonthOfWarranty());
        statement.setString(6,product.getNote());
        statement.setLong(7,product.getSupplierId());
        statement.setLong(8,product.getCategoryId());
    }

    private void setProduct(Product product, ResultSet resultSet) throws SQLException {
        product.setId(resultSet.getLong("id"));
        product.setProductCode(resultSet.getString("product_code"));
        product.setName(resultSet.getString("name"));
        product.setPrice(resultSet.getDouble("price"));
        product.setStockQuantity(resultSet.getInt("stock_quantity"));
        product.setMonthOfWarranty(resultSet.getInt("month_of_warranty"));
        product.setNote(resultSet.getString("note"));
        product.setSupplierId(resultSet.getLong("supplier_id"));
        product.setCategoryId(resultSet.getLong("category_id"));
    }

    @Override
    public Product getByName(String name) {
        Product product = null;

        String query = "SELECT * FROM products WHERE name LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + name + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    product = new Product();
                    setProduct(product, resultSet);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public int getWeeklyNewProduct() {
        int count = 0;

        String query = "SELECT COUNT(*) AS new_products_count FROM products" +
                " WHERE created_at >= DATE_TRUNC('week', CURRENT_DATE)" +
                " AND created_at < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '1 week';";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                count = rs.getInt("new_products_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void updateExportQuantity(Long productId, int exportQuantity) {
        try {
            String selectQuery = "SELECT stock_quantity FROM products WHERE id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, productId);
                ResultSet resultSet = selectStatement.executeQuery();

                int currentStockQuantity = 0;
                if (resultSet.next()) {
                    currentStockQuantity = resultSet.getInt("stock_quantity");
                }

                int newStockQuantity = currentStockQuantity - exportQuantity;

                String updateQuery = "UPDATE products SET stock_quantity = ? WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, newStockQuantity);
                    updateStatement.setLong(2, productId);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Product, Integer> productTopMonthSellingByQuantity() {
        Map<Product, Integer> productExportMap = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();

        String query = "SELECT p.id AS product_id, p.name AS product_name, SUM(ps.export_quantity) AS total_export_quantity " +
                "FROM products p " +
                "INNER JOIN products_storage ps ON p.id = ps.product_id " +
                "WHERE EXTRACT(MONTH FROM ps.created_at) = ? " +
                "AND EXTRACT(YEAR FROM ps.created_at) = ? " +
                "GROUP BY p.id, p.name " +
                "ORDER BY total_export_quantity DESC " +
                "LIMIT 5";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentMonth.getValue());
            statement.setInt(2, currentYear);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long productId = resultSet.getLong("product_id");
                    int totalExportQuantity = resultSet.getInt("total_export_quantity");

                    Product product = getById(productId);
                    productExportMap.put(product, totalExportQuantity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productExportMap;
    }

    @Override
    public Map<Product, Double> productTopMonthSellingByRevenue() {
        Map<Product, Double> productRevenueMap = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();

        String query = "SELECT p.id AS product_id, p.name AS product_name, " +
                "SUM(od.total_amount) AS total_revenue " +
                "FROM products p " +
                "INNER JOIN order_detail od ON p.id = od.product_id " +
                "INNER JOIN orders o ON od.order_id = o.id " +
                "WHERE EXTRACT(MONTH FROM o.order_date) = ? " +
                "AND EXTRACT(YEAR FROM o.order_date) = ? " +
                "GROUP BY p.id, p.name " +
                "ORDER BY total_revenue DESC " +
                "LIMIT 5";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, currentMonth.getValue());
            statement.setInt(2, currentYear);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long productId = resultSet.getLong("product_id");
                    double totalRevenue = resultSet.getDouble("total_revenue");

                    Product product = getById(productId);
                    productRevenueMap.put(product, totalRevenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productRevenueMap;
    }

    @Override
    public Map<Product, Double> productTopMonthSellingByRevenueFromTo(LocalDate fromDate, LocalDate toDate) {
        Map<Product, Double> productRevenueMap = new HashMap<>();

        String query = "SELECT p.id AS product_id, p.name AS product_name, " +
                "SUM(od.total_amount) AS total_revenue " +
                "FROM products p " +
                "INNER JOIN order_detail od ON p.id = od.product_id " +
                "INNER JOIN orders o ON od.order_id = o.id " +
                "WHERE o.order_date BETWEEN ? AND ? " +
                "GROUP BY p.id, p.name " +
                "ORDER BY total_revenue DESC " +
                "LIMIT 5";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long productId = resultSet.getLong("product_id");
                    double totalRevenue = resultSet.getDouble("total_revenue");

                    Product product = getById(productId);
                    productRevenueMap.put(product, totalRevenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productRevenueMap;
    }

    @Override
    public List<Product> getByQuantityBelow(int quantity) {
        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM products WHERE stock_quantity <= ? ORDER BY stock_quantity DESC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    setProduct(product, resultSet);
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }


}
