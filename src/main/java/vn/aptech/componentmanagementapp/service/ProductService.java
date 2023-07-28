package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.ProductDAO;
import vn.aptech.componentmanagementapp.dao.ProductDAOImpl;
import vn.aptech.componentmanagementapp.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public List<Product> getAllProduct() {
        return productDAO.getAll();
    }
    public Product getProductById(long productId) {
        return productDAO.getById(productId);
    }

    public Product getProductByName(String productName) {
        return productDAO.getByName(productName);
    }

    public void addProduct(Product product) {
        productDAO.add(product);
    }

    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    public void deleteProduct(long productId) {
        productDAO.delete(productId);
    }

    public int getWeeklyNewProduct() { return productDAO.getWeeklyNewProduct(); }

    public void updateProductExportQuantity(Long productId, int exportQuantity) {
        productDAO.updateExportQuantity(productId, exportQuantity);
    }

    public Map<Product, Integer> getProductTopMonthSellingByQuantity() {
        return productDAO.productTopMonthSellingByQuantity();
    }

    public Map<Product, Integer> getProductTopSellingByQuantityFromTo(LocalDate fromDate, LocalDate toDate) {
        return productDAO.productTopSellingByQuantityFromTo(fromDate, toDate);
    }

    public Map<Product, Double> getProductTopMonthSellingByRevenue() {
        return productDAO.productTopMonthSellingByRevenue();
    }

    public Map<Product, Double> getProductTopSellingByRevenueFromTo(LocalDate fromDate, LocalDate toDate) {
        return productDAO.productTopSellingByRevenueFromTo(fromDate, toDate);
    }

    public List<Product> getProductByQuantityBelow(int quantity) {
        return productDAO.getByQuantityBelow(quantity);
    }


}
