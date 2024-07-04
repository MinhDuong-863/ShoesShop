package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

public interface IProductService {
    Product createProduct(ProductDTO product) throws DataNotFoundException;
    Product getProductById(int id) throws DataNotFoundException;
    Page<ProductResponse> getProducts(String keyWord, int categoryId, PageRequest pageRequest);
    Product updateProduct(int id, ProductDTO product) throws DataNotFoundException;
    void deleteProduct(int id);
    boolean existsByName(String name);
    ProductImage createProductImage(int productId, ProductImageDTO productImageDTO) throws Exception;
}
