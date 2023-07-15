package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Position;

import java.util.List;

public interface PositionDAO extends BaseDAO<Position> {
    Position getById(long positionId);
    List<Position> getAll();
}
