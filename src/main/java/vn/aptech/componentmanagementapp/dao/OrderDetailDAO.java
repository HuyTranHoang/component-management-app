package vn.aptech.componentmanagementapp.dao;

import vn.aptech.componentmanagementapp.model.Order;
import vn.aptech.componentmanagementapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailDAO extends BaseDAO<OrderDetail> {
    List<OrderDetail> getAllByOrderId(long orderId);
}
