package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.OrderDetailDAO;
import vn.aptech.componentmanagementapp.dao.OrderDetailDAOImpl;
import vn.aptech.componentmanagementapp.model.OrderDetail;

import java.util.List;

public class OrderDetailService {
    private OrderDetailDAO orderDetailDAO;

    public OrderDetailService() {
        this.orderDetailDAO = new OrderDetailDAOImpl();
    }

    public List<OrderDetail> getAllOrderDetail() {
        return orderDetailDAO.getAll();
    }

    public OrderDetail getOrderDetailById(long orderDetailId) {
        return orderDetailDAO.getById(orderDetailId);
    }

    public void addOrderDetail(OrderDetail product) {
        orderDetailDAO.add(product);
    }

    public void updateOrderDetail(OrderDetail product) {
        orderDetailDAO.update(product);
    }

    public void deleteOrderDetail(long orderDetailId) {
        orderDetailDAO.delete(orderDetailId);
    }
}
