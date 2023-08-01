package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.ProductStorage;

import java.time.LocalDate;
import java.util.List;

public interface ProductStorageDAO extends BaseDAO<ProductStorage>{
    List<ProductStorage> getAllFromTo(LocalDate fromDate, LocalDate toDate);
}
