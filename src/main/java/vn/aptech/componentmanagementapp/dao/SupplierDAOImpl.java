package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAOImpl implements SupplierDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public Supplier getById(long supplierId) {
        Supplier supplier = null;

        String query = "SELECT * FROM suppliers WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, supplierId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    supplier = new Supplier();
                    setSupplier(supplier, resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return supplier;
    }

    @Override
    public List<Supplier> getAll() {
        String query = "SELECT * FROM suppliers";
        ArrayList<Supplier> suppliers = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                setSupplier(supplier, resultSet);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    @Override
    public void add(Supplier supplier) {
        String query = "INSERT INTO suppliers (name, email, website) " + "VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statementInsertUpdate(supplier, statement);
            if (supplier.getEmail().isEmpty()) {
                statement.setNull(2, Types.VARCHAR);
            }
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    supplier.setId(generatedId);
                } else {
                    throw new SQLException("Adding supplier failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Supplier supplier) {
        String query = "UPDATE suppliers SET name = ?, email = ?, website = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(supplier, statement);
            statement.setLong(4, supplier.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long supplierId) {
        String query = "DELETE FROM suppliers WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,supplierId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Supplier supplier, PreparedStatement statement) throws SQLException {
        statement.setString(1,supplier.getName());
        statement.setString(2, supplier.getEmail());
        statement.setString(3, supplier.getWebsite());
    }

    private void setSupplier(Supplier supplier, ResultSet resultSet) throws SQLException {
        supplier.setId(resultSet.getLong("id"));
        supplier.setName(resultSet.getString("name"));
        supplier.setEmail(resultSet.getString("email"));
        supplier.setWebsite(resultSet.getString("website"));
    }
}
