package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.ProductStorageDAO;
import vn.aptech.componentmanagementapp.dao.ProductStorageImpl;
import vn.aptech.componentmanagementapp.model.ProductStorage;

import java.time.LocalDate;
import java.util.List;

public class ProductStorageService {
    private ProductStorageDAO productStorageDAO;

    public ProductStorageService() {
        this.productStorageDAO = new ProductStorageImpl();
    }

    public List<ProductStorage> getAllProductStorage() {
        return productStorageDAO.getAll();
    }

    public ProductStorage getProductStorageById(long productStorageId) {
        return productStorageDAO.getById(productStorageId);
    }

    public void addProductStorage(ProductStorage productStorage) {
        productStorageDAO.add(productStorage);
    }

    public void updateProductStorage(ProductStorage productStorage) {
        productStorageDAO.update(productStorage);
    }

    public void deleteProductStorage(int productStorageId) {
        productStorageDAO.delete(productStorageId);
    }
    public List<ProductStorage> getAllProductStorageFromTo(LocalDate fromDate, LocalDate toDate) {
        return productStorageDAO.getAllFromTo(fromDate, toDate);
    }

}
