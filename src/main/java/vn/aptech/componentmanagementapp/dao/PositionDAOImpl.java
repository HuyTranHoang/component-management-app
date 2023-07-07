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
                position.setId(resultSet.getInt("id"));
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
    public void add(Position entity) {

    }

    @Override
    public void update(Position entity) {

    }

    @Override
    public void delete(long id) {

    }
}
