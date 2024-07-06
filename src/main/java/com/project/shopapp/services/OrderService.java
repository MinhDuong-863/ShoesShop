package com.project.shopapp.services;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderDetailResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //check user_id exists
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        //convert order --> orderDTO
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cập nhật các trường thông tin cho order
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        //Shipping date behind the order date
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now().plusDays(3) : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new Exception("Shipping date must be after the order date");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);
        //List order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            int productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            //Check product exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));
            //Set order detail
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }
    @Override
    public Order getOrder(int id) {
        return orderRepository.findById(id).orElse(null);
    }
    @Override
    public List<Order> getOrdersByUserid(int userId) {
        return orderRepository.findByUserId(userId);
    }
    @Override
    @Transactional
    public Order updateOrder(int id, OrderDTO orderDTO) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        //Tao luong anh xa tu OrderDTO --> Order
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cap nhat thong tin order
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);
        orderRepository.save(existingOrder);
        return existingOrder;
    }
    @Override
    @Transactional
    public void deleteOrder(int id) {
        Optional<Order> order = orderRepository.findById(id);
        //Khong xoa cung order --> Xoa mem
        if(order.isPresent()){
            order.get().setActive(false);
            orderRepository.save(order.get());
        }
    }
}
