package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data // --> Chuyển thành ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 350, message = "Name must be between 3 and 350 characters")
    private String productName;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Float price;
    private String thumbnail;
    private String description;
    private Date createAt;
    private Date updateAt;
    private MultipartFile file;
    @NotBlank(message = "Category is required")
    @JsonProperty("category_id")
    private String categoryId;
}
