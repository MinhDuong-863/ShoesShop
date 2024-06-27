package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.CRUDOrderDetailResponse;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.services.IOrderDetailService;
import com.project.shopapp.services.OrderDetailService;
import com.project.shopapp.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;
    @PostMapping
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO){
        try {
            OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDOrderDetailResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.CREATE_DETAIL_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") int id){
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(
                    CRUDOrderDetailResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.GET_DETAIL_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getListOrderDetails(
            @Valid @PathVariable("orderId") int orderId){
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") int id, @RequestBody OrderDetailDTO orderDetailDTO){
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDOrderDetailResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_DETAIL_ORDER_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<CRUDOrderDetailResponse> deleteOrderDetail(
            @Valid @PathVariable("id") int id){
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok(
                CRUDOrderDetailResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_DETAIL_ORDER_SUCCESS))
                        .build()
        );
    }
}
