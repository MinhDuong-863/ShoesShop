package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        //Kiem tra order co ton tai
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        //Kiem tra product co ton tai
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(orderDetailDTO.getQuantity())
                .price(product.getPrice())
                .totalMoney(orderDetailDTO.getQuantity() * product.getPrice())
                .color(orderDetailDTO.getColor())
                .build();
        orderDetailRepository.save(orderDetail);
        return orderDetail;
    }

    @Override
    public OrderDetail getOrderDetail(int id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order detail not found"));
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(int id) {
        return orderDetailRepository.findByOrderId(id);
    }

    @Override
    public OrderDetail updateOrderDetail(int id, OrderDetailDTO orderDetailDTO) throws Exception {
        //Kiem tra order detail co ton tai
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order detail not found"));
        //Kiem tra order co ton tai
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        //Kiem tra product co ton tai
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        existingOrderDetail.setOrder(order);
        existingOrderDetail.setProduct(product);
        existingOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getQuantity() * orderDetailDTO.getPrice());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        orderDetailRepository.save(existingOrderDetail);
        return existingOrderDetail;
    }

    @Override
    public void deleteOrderDetail(int id) {
        orderDetailRepository.deleteById(id);
    }
}
