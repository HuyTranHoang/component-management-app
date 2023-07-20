package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Order;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public interface OrderDAO extends BaseDAO<Order> {
    long addReturnId(Order order);
    Map<LocalDate, Double> weeklyTotalAmounts(LocalDate startOfWeek, LocalDate endOfWeek);
    int getWeeklyNewOrder();
}
