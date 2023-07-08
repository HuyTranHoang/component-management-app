package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                product.setDescription(resultSet.getString("description"));
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
    public void add(Product entity) {

    }

    @Override
    public void update(Product entity) {

    }

    @Override
    public void delete(long id) {

    }
}
