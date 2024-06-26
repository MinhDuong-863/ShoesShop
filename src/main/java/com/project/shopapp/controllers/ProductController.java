package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.CRUDProductResponse;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;
    @GetMapping("") //http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam("page") int page, @RequestParam("limit") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createAt").descending());
        Page<ProductResponse> productPage = productService.getProducts(pageRequest);
        //Get total pages
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        ProductListResponse productListResponse = ProductListResponse.builder()
                .productResponses(products)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok(productListResponse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id){
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDProductResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.GET_PRODUCT_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @RequestBody @Valid ProductDTO productDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Product product = productService.createProduct(productDTO);
            return ResponseEntity.ok("Hi, insertProduct" + product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDProductResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.CREATE_PRODUCT_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @ModelAttribute("files") List<MultipartFile> files,
            @PathVariable("id") int id) {
        try {
            Product existingProduct = productService.getProductById(id);
            List<ProductImage> listProductImages = new ArrayList<>();

            files = files == null ? new ArrayList<MultipartFile>() : files;
            for (MultipartFile file : files) {
                //Kiểm tra kích thước và định dạng
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is to large! Maximun size is 10MB");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image!");
                }
                //Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file);
                ProductImageDTO productImageDTO = ProductImageDTO.builder()
                        .productId(existingProduct.getId())
                        .imageUrl(filename)
                        .build();
                ProductImage productImage = productService
                        .createProductImage(existingProduct.getId(), productImageDTO);
                listProductImages.add(productImage);
            }
            return ResponseEntity.ok(listProductImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody ProductDTO productDTO) {
        try {
            Product product = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDProductResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_PRODUCT_FAILED, e.getMessage()))
                            .build()
            );
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<CRUDProductResponse> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(
                    CRUDProductResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_PRODUCT_SUCCESS))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CRUDProductResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_PRODUCT_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    //@PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(100000, 10000000))
                    .thumbnail(faker.lorem().sentence())
                    .description(faker.lorem().sentence())
                    .categoryId((int)faker.number().numberBetween(1, 4))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Generate fake products successfully!");
    }
    private String storeFile(MultipartFile file) throws IOException {
        if(!isImage(file) && file.getOriginalFilename() != null){
            throw new IOException("File must be an image!");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //Thêm UUID để tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        //Đường dẫn đến nơi muốn lưu file
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
