package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.ProductDAO;
import vn.aptech.componentmanagementapp.dao.ProductDAOImpl;

import java.time.LocalDate;

public class ProductStorage {
    private long id;
    private int importQuantity;
    private int exportQuantity;
    private LocalDate dateOfStorage;
    private long productId;
    private Product product;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(int importQuantity) {
        this.importQuantity = importQuantity;
    }

    public int getExportQuantity() {
        return exportQuantity;
    }

    public void setExportQuantity(int exportQuantity) {
        this.exportQuantity = exportQuantity;
    }

    public LocalDate getDateOfStorage() {
        return dateOfStorage;
    }

    public void setDateOfStorage(LocalDate dateOfStorage) {
        this.dateOfStorage = dateOfStorage;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        if (product == null) {
            // Perform lazy loading for Position object
            ProductDAO productDAO = new ProductDAOImpl();
            product = productDAO.getById(productId);
        }
        return product;
    }

}
