package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.OrderDAO;
import vn.aptech.componentmanagementapp.dao.OrderDAOImpl;
import vn.aptech.componentmanagementapp.dao.ProductDAO;
import vn.aptech.componentmanagementapp.dao.ProductDAOImpl;

public class OrderDetail {
    private long id;
    private String name;
    private double price;
    private int quantity;
    private int discount;
    private double totalAmount;
    private long orderId;
    private long productId;

    // Lazy-loaded Order and Product objects
    private Order order;
    private Product product;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Order getOrder() {
        if (order == null) {
            // Perform lazy loading for Order object
            OrderDAO orderDAO = new OrderDAOImpl();
            order = orderDAO.getById(orderId);
        }
        return order;
    }

    public Product getProduct() {
        if (product == null) {
            // Perform lazy loading for Order object
            ProductDAO productDAO = new ProductDAOImpl();
            product = productDAO.getById(productId);
        }
        return product;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", totalAmount=" + totalAmount +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", order=" + order +
                ", product=" + product +
                '}';
    }
}
