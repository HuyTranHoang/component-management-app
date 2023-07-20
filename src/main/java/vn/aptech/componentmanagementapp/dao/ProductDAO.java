package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;

public interface ProductDAO extends BaseDAO<Product> {
    Product getByName(String name);
    int getWeeklyNewProduct();
}
