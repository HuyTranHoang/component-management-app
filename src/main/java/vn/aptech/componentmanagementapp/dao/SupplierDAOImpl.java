package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Supplier;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAOImpl implements SupplierDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public Supplier getById(long id) {
        return null;
    }

    @Override
    public List<Supplier> getAll() {
        String query = "SELECT * FROM suppliers";
        ArrayList<Supplier> suppliers = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(resultSet.getLong("id"));
                supplier.setName(resultSet.getString("name"));
                supplier.setEmail(resultSet.getString("email"));
                supplier.setWebsite(resultSet.getString("website"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    @Override
    public void add(Supplier entity) {

    }

    @Override
    public void update(Supplier entity) {

    }

    @Override
    public void delete(long id) {

    }
}
