package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.ProductDAO;
import vn.aptech.componentmanagementapp.dao.ProductDAOImpl;

import java.time.LocalDate;

public class ProductStorage {
    private long id;
    private int ImportQuantity;
    private int ExportQuantity;
    private LocalDate DateOfStorage;
    private long ProductId;

    private Product product;

    private long productId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImportQuantity() {
        return ImportQuantity;
    }

    public void setImportQuantity(int importQuantity) {
        ImportQuantity = importQuantity;
    }

    public int getExportQuantity() {
        return ExportQuantity;
    }

    public void setExportQuantity(int exportQuantity) {
        ExportQuantity = exportQuantity;
    }

    public LocalDate getDateOfStorage() {
        return DateOfStorage;
    }

    public void setDateOfStorage(LocalDate date) {
        DateOfStorage = date;
    }

    public long getProductId() {
        return ProductId;
    }

    public void setProductId(long productId) {
        ProductId = productId;
    }

    public Product getProduct() {
        if (product == null) {
            // Perform lazy loading for Position object
            ProductDAO productDAO = new ProductDAOImpl();
            product = productDAO.getById(productId);
        }
        return product;
    }

    @Override
    public String toString() {
        return "ProductStorage{" +
                "id=" + id +
                ", ImportQuantity=" + ImportQuantity +
                ", ExportQuantity=" + ExportQuantity +
                ", Date=" + DateOfStorage +
                ", ProductId=" + ProductId +
                ", product=" + product +
                ", productId=" + productId +
                '}';
    }
}
