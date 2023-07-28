package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.OrderDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDetailDAO extends BaseDAO<OrderDetail> {
    List<OrderDetail> getAllByOrderId(long orderId);

    Map<String, Double> getTotalAmountByCategory(LocalDate fromDate, LocalDate toDate);
}
