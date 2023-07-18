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
}
