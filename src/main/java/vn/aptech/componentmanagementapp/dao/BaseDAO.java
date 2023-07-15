package vn.aptech.componentmanagementapp.dao;

import java.util.List;

public interface BaseDAO<T> {
    T getById(long id);
    List<T> getAll();
    void add(T entity);
    void update(T entity);
    void delete(long id);
}
