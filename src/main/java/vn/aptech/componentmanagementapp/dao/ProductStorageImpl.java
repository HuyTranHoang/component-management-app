package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.ProductStorage;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductStorageImpl implements ProductStorageDAO{
    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public ProductStorage getById(long productStorageId) {
        ProductStorage productStorage = null;

        String query = "SELECT * FROM products_storage WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, productStorageId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    productStorage = new ProductStorage();
                    setProductStorage(productStorage, resultSet);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productStorage;
    }

    @Override
    public List<ProductStorage> getAll() {
        String query = "SELECT * FROM products_storage ORDER BY id";
        ArrayList<ProductStorage> productStorages = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                ProductStorage productStorage = new ProductStorage();
                setProductStorage(productStorage, resultSet);
                productStorages.add(productStorage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productStorages;
    }

    @Override
    public void add(ProductStorage productStorage) {
        String query = "INSERT INTO products_storage(import_quantity, export_quantity, product_id) " +
                "VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(productStorage, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ProductStorage productStorage) {
        String query = "UPDATE products_storage SET import_quantity = ?, export_quantity = ?, product_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(productStorage, statement);
            statement.setLong(4, productStorage.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long productStorageId) {
        String query = "DELETE FROM products_storage WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, productStorageId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(ProductStorage productStorage, PreparedStatement statement) throws SQLException{
        statement.setInt(1, productStorage.getImportQuantity());
        statement.setInt(2, productStorage.getExportQuantity());
        statement.setLong(3, productStorage.getProductId());
    }

    private void setProductStorage(ProductStorage productStorage, ResultSet resultSet) throws SQLException {
        productStorage.setId(resultSet.getLong("id"));
        productStorage.setImportQuantity(resultSet.getInt("import_quantity"));
        productStorage.setExportQuantity(resultSet.getInt("export_quantity"));
        productStorage.setProductId(resultSet.getLong("product_id"));
        productStorage.setDateOfStorage(resultSet.getDate("created_at").toLocalDate());
    }

    @Override
    public List<ProductStorage> getAllFromTo(LocalDate fromDate, LocalDate toDate) {
        String query = "SELECT * FROM products_storage WHERE created_at BETWEEN ? AND ? ORDER BY id";
        ArrayList<ProductStorage> productStorages = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fromDate));
            statement.setDate(2, java.sql.Date.valueOf(toDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ProductStorage productStorage = new ProductStorage();
                    setProductStorage(productStorage, resultSet);
                    productStorages.add(productStorage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productStorages;
    }
}
