package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.OrderDetailDAO;
import vn.aptech.componentmanagementapp.dao.OrderDetailDAOImpl;
import vn.aptech.componentmanagementapp.model.OrderDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    public List<OrderDetail> getAllOrderDetailByOrderId(long orderId) { return orderDetailDAO.getAllByOrderId(orderId);}

    public Map<String, Double> getOrderDetailTotalAmountByCategory(LocalDate fromDate, LocalDate toDate) {
        return orderDetailDAO.getTotalAmountByCategory(fromDate, toDate);
    }

    public Map<String, Integer> getOrderDetailTotalQuantityByCategory(LocalDate fromDate, LocalDate toDate) {
        return orderDetailDAO.getTotalQuantityByCategory(fromDate, toDate);
    }
}
