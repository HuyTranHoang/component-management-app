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

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetailDAO.add(orderDetail);
    }

    public void updateOrderDetail(OrderDetail orderDetail) {
        orderDetailDAO.update(orderDetail);
    }

    public void deleteOrderDetail(long orderDetailId) {
        orderDetailDAO.delete(orderDetailId);
    }
}
