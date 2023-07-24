package vn.aptech.componentmanagementapp.service;

import vn.aptech.componentmanagementapp.dao.OrderDAO;
import vn.aptech.componentmanagementapp.dao.OrderDAOImpl;
import vn.aptech.componentmanagementapp.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OrderService {
    private OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAOImpl();
    }

    public List<Order> getAllOrder() {
        return orderDAO.getAll();
    }

    public Order getOrderById(long orderId) {
        return orderDAO.getById(orderId);
    }

    public void addOrder(Order order) {
        orderDAO.add(order);
    }

    public long addOrderReturnId(Order order) {return orderDAO.addReturnId(order);}

    public void updateOrder(Order order) {
        orderDAO.update(order);
    }

    public void deleteOrder(long orderId) {
        orderDAO.delete(orderId);
    }

    public Map<LocalDate, Double> getWeeklyTotalAmounts(LocalDate startOfWeek, LocalDate endOfWeek) {
        return orderDAO.weeklyTotalAmounts(startOfWeek, endOfWeek);
    }
    public int getWeeklyNewOrder() { return orderDAO.weeklyNewOrder(); }
    public double getTodayTotalAmount() { return orderDAO.todayTotalAmount(); }
    public double getYesterdayTotalAmount() { return orderDAO.yesterdayTotalAmount(); }

    public void cancelOrder(long orderId) { orderDAO.cancelOrder(orderId); }
}
