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
                product.setMinimumPrice(resultSet.getDouble("minimum_price"));
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
        String query = "INSERT INTO products(id, product_code, name, price, minimum_price, stock_quantity, month_of_warranty, note, " +
                "description, supplier_id, category_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,product.getId());
            statement.setString(2,product.getProductCode());
            statement.setString(3,product.getName());
            statement.setDouble(4,product.getPrice());
            statement.setDouble(5,product.getMinimumPrice());
            statement.setInt(6,product.getStockQuantity());
            statement.setInt(7,product.getMonthOfWarranty());
            statement.setString(8,product.getNote());
            statement.setLong(9,product.getSupplierId());
            statement.setLong(10,product.getCategoryId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product product) {
        String query = "UPDATE products SET product_code = ?, name = ?, price = ?, minimum_price = ?, stock_quantity = ?, month_of_warranty = ?, note = ?, " +
                "supplier_id = ?, category_id = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,product.getProductCode());
            statement.setString(2,product.getName());
            statement.setDouble(3,product.getPrice());
            statement.setDouble(4,product.getMinimumPrice());
            statement.setInt(5,product.getStockQuantity());
            statement.setInt(6,product.getMonthOfWarranty());
            statement.setString(7,product.getNote());
            statement.setLong(9,product.getSupplierId());
            statement.setLong(10,product.getCategoryId());
            statement.setLong(11,product.getId());
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
}
