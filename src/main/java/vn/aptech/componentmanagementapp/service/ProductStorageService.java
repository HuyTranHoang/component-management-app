package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.ProductStorageDAO;
import vn.aptech.componentmanagementapp.dao.ProductStorageImpl;
import vn.aptech.componentmanagementapp.dao.SupplierDAO;
import vn.aptech.componentmanagementapp.dao.SupplierDAOImpl;
import vn.aptech.componentmanagementapp.model.ProductStorage;
import vn.aptech.componentmanagementapp.model.Supplier;

import java.util.List;

public class ProductStorageService {
    private ProductStorageDAO productStorageDAO;

    public ProductStorageService() {
        this.productStorageDAO = new ProductStorageImpl();
    }

    public List<ProductStorage> getAllProductStorage() {
        return productStorageDAO.getAll();
    }

}
