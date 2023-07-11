package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Category;
import vn.aptech.componentmanagementapp.model.Department;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;


import java.sql.*;
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
    public void add(Category category) {
        String query = "INSERT INTO categories (name, description) " +
                "VALUES (?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(category, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Category category) {
        String query = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(category, statement);
            statement.setLong(3, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Category category, PreparedStatement statement) throws SQLException {
        statement.setString(1,category.getName());
        statement.setString(2, category.getDescription());;
    }

    @Override
    public void delete(long categoryId) {
        String query = "DELETE FROM categories WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,categoryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
