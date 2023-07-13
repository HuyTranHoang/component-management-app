package vn.aptech.componentmanagementapp.dao;


import vn.aptech.componentmanagementapp.model.Position;
import vn.aptech.componentmanagementapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAOImpl implements PositionDAO{

    private Connection connection = DatabaseConnection.getConnection();

    @Override
    public Position getById(long positionId) {
        Position position = null;

        String query = "SELECT * FROM positions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, positionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    position = new Position();
                    position.setId(resultSet.getInt("id"));
                    position.setName(resultSet.getString("name"));
                    position.setDescription(resultSet.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return position;
    }

    @Override
    public List<Position> getAll() {
        String query = "SELECT * FROM positions";
        ArrayList<Position> positions = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Position position = new Position();
                position.setId(resultSet.getLong("id"));
                position.setName(resultSet.getString("name"));
                position.setDescription(resultSet.getString("description"));
                positions.add(position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return positions;
    }

    @Override
    public void add(Position position) {
        String query = "INSERT INTO positions (id,name,description) VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(position, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Position position) {
        String query = "UPDATE positions SET id = ?, name = ?, description = ? WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statementInsertUpdate(position, statement);
            statement.setLong(4,position.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void statementInsertUpdate(Position position, PreparedStatement statement) throws SQLException {
        statement.setLong(1,position.getId());
        statement.setString(2, position.getName());
        statement.setString(3, position.getDescription());
    }

    @Override
    public void delete(long positionId) {
        String query = "DELETE FROM employees WHERE id = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,positionId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
