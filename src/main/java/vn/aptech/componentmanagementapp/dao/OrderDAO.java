package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Order;

import java.time.LocalDate;
import java.util.Map;

public interface OrderDAO extends BaseDAO<Order> {
    long addReturnId(Order order);
    Map<LocalDate, Double> weeklyTotalAmounts(LocalDate startOfWeek, LocalDate endOfWeek);
    int weeklyNewOrder();
    double todayTotalAmount();
    double yesterdayTotalAmount();
    void cancelOrder(long orderId);
    int countOrder(LocalDate fromDate, LocalDate toDate);
    double sumTotalAmount(LocalDate fromDate, LocalDate toDate);
    int countCustomer(LocalDate fromDate, LocalDate toDate);
    int countCanceledOrder(LocalDate fromDate, LocalDate toDate);
}
