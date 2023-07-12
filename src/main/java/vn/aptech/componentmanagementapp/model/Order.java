package vn.aptech.componentmanagementapp.model;

import vn.aptech.componentmanagementapp.dao.*;

import java.time.LocalDateTime;

public class Order {
    private long id;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime shipmentDate;
    private String deliveryLocation;
    private double totalAmount;
    private String note;
    private long customerId;
    private long employeeId;

    // Lazy-loaded Customer and Employee objects
    private Customer customer;
    private Employee employee;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public Customer getCustomer() {
        if (customer == null) {
            // Perform lazy loading for Position object
            CustomerDAO customerDAO = new CustomerDAOImpl();
            customer = customerDAO.getById(customerId);
        }
        return customer;
    }

    public Employee getEmployee() {
        if (employee == null) {
            // Perform lazy loading for Position object
            EmployeeDAO employeeDAO = new EmployeeDAOImpl();
            employee = employeeDAO.getById(employeeId);
        }
        return employee;
    }
}
