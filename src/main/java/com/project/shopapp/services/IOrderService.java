package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO order) throws Exception;
    Object getOrder(int id);
    List<Order> getOrdersByUserid(int userId);
    Order updateOrder(int id, OrderDTO order) throws DataNotFoundException;
    void deleteOrder(int id);
}
