package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data // --> Chuyển thành ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "order id must be > 0")
    private int orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "product id must be > 0")
    private int productId;

    @Min(value = 1, message = "quantity id must be > 0")
    private int quantity;

    private Float price;

    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
