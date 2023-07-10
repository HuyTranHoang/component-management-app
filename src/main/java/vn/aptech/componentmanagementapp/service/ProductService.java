package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.ProductDAO;
import vn.aptech.componentmanagementapp.dao.ProductDAOImpl;
import vn.aptech.componentmanagementapp.model.Product;

import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public List<Product> getAllProduct() {
        return productDAO.getAll();
    }
}
