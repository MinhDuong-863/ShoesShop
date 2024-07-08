package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.CRUDOrderResponse;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.IOrderService;
import com.project.shopapp.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderDTO orderDTO, BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        CRUDOrderResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKey.CREATE_ORDER_FAILED, errorMessage))
                                .build()
                );
            }
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    CRUDOrderResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.CREATE_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") int userId){
        try{
            List<Order> orders = orderService.getOrdersByUserid(userId);
            return ResponseEntity.ok(orders);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    CRUDOrderResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.GET_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") int id){
        try{
            Order existingOrder = (Order) orderService.getOrder(id);
            return ResponseEntity.ok(OrderResponse.fromOrder(existingOrder));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    CRUDOrderResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.GET_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable("id") int id,
            @Valid @RequestBody OrderDTO orderDTO){
        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    CRUDOrderResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable("id") int id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok(
                CRUDOrderResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_ORDER_SUCCESS))
                        .build()
        );
    }
}
