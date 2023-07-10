package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.PositionDAO;
import vn.aptech.componentmanagementapp.dao.PositionDAOImpl;
import vn.aptech.componentmanagementapp.model.Position;

import java.util.List;

public class PositionService {
    private PositionDAO positionDAO;

    public PositionService() {
        this.positionDAO = new PositionDAOImpl();
    }

    public List<Position> getAllDepartment() {
        return positionDAO.getAll();
    }
    public Position getPositionById(int positionId) {
        return positionDAO.getById(positionId);
    }

    public void addPosition(Position position) {

        positionDAO.add(position);
    }

    public void updatePosition(Position position) {

        positionDAO.update(position);
    }

    public void deletePosition(int positionId) {

        positionDAO.delete(positionId);
    }
}
