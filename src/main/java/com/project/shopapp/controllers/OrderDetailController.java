package com.project.shopapp.controllers;

import com.project.shopapp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {
    @PostMapping
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO){
        return ResponseEntity.ok("Create order detail");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") int id){
        return ResponseEntity.ok("Get order detail");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getListOrderDetails(
            @Valid @PathVariable("orderId") int orderId){
        return ResponseEntity.ok("Get lisst order detail");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") int id, @RequestBody OrderDetailDTO orderDetailDTO){
        return ResponseEntity.ok("Update order detail" + id + " " + orderDetailDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(
            @Valid @PathVariable("id") int id){
        return ResponseEntity.noContent().build();
    }
}
