package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProductDAO extends BaseDAO<Product> {
    Product getByName(String name);

    int getWeeklyNewProduct();
    void updateImportQuantity(Long productId, int importQuantity);
    void updateExportQuantity(Long productId, int exportQuantity);

    Map<Product, Integer> productTopMonthSellingByQuantity();

    Map<Product, Integer> productTopSellingByQuantityFromTo(LocalDate fromDate, LocalDate toDate);

    Map<Product, Double> productTopMonthSellingByRevenue();

    Map<Product, Double> productTopSellingByRevenueFromTo(LocalDate fromDate, LocalDate toDate);

    List<Product> getByQuantityBelow(int quantity);
}
