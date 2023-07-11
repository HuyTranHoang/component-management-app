package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Product getById(long id) {
        return null;
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products";
        ArrayList<Product> products = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getLong("id"));
                product.setProductCode(resultSet.getString("product_code"));
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStockQuantity(resultSet.getInt("stock_quantity"));
                product.setMonthOfWarranty(resultSet.getInt("month_of_warranty"));
                product.setNote(resultSet.getString("note"));
                product.setSupplierId(resultSet.getLong("supplier_id"));
                product.setCategoryId(resultSet.getLong("category_id"));
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
}
