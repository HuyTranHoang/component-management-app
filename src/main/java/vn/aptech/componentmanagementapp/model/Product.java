package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.*;

public class Product {
    private long id;
    private String productCode;
    private String name;
    private double price;
    private int stockQuantity;
    private int monthOfWarranty;
    private String note;
    private long supplierId;
    private long categoryId;

    // Lazy-loaded Supplier and Category objects
    private Supplier supplier;
    private Category category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMonthOfWarranty() {
        return monthOfWarranty;
    }

    public void setMonthOfWarranty(int monthOfWarranty) {
        this.monthOfWarranty = monthOfWarranty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public Supplier getSupplier() {
        if (supplier == null) {
            // Perform lazy loading for Category object
            SupplierDAO supplierDAO = new SupplierDAOImpl();
            supplier = supplierDAO.getById(supplierId);
        }
        return supplier;
    }

    public Category getCategory() {
        if (category == null) {
            // Perform lazy loading for Category object
            CategoryDAO categoryDAO = new CategoryDAOImpl();
            category = categoryDAO.getById(categoryId);
        }
        return category;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", monthOfWarranty=" + monthOfWarranty +
                ", note='" + note + '\'' +
                ", supplierId=" + supplierId +
                ", categoryId=" + categoryId +
                ", supplier=" + supplier +
                ", category=" + category +
                '}';
    }
}
