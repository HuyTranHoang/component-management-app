package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductDAO extends BaseDAO<Product> {
    Product getByName(String name);
    int getWeeklyNewProduct();
    void updateExportQuantity(Long productId, int exportQuantity);

    Map<Product, Integer> productTopMonthSellingByQuantity();

    Map<Product, Double> productTopMonthSellingByRevenue();

    List<Product> getByQuantityBelow(int quantity);
}
