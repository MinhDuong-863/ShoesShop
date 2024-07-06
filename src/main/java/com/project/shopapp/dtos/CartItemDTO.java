package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data // --> Chuyển thành ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    @JsonProperty("product_id")
    private int productId;
    @JsonProperty("quantity")
    private int quantity;
}
