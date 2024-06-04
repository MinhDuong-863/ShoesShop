package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail getOrderDetail(int id) throws DataNotFoundException;
    List<OrderDetail> getOrderDetailsByOrderId(int id);
    OrderDetail updateOrderDetail(int id, OrderDetailDTO orderDetailDTO) throws Exception;
    void deleteOrderDetail(int id);
}
