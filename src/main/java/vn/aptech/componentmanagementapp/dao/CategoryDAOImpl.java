package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO{

    private Connection connection = DatabaseConnection.getConnection();
    @Override
    public Category getById(long id) {
        return null;
    }

    @Override
    public List<Category> getAll() {
        String query = "SELECT * FROM categories";
        ArrayList<Category> categories = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getLong("id"));
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public void add(Category entity) {

    }

    @Override
    public void update(Category entity) {

    }

    @Override
    public void delete(long id) {

    }
}
