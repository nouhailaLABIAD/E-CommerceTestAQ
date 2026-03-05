package com.example.ecommerce.service.interfaces;

import java.util.List;

import com.example.ecommerce.entity.Product;

public interface ProductService {

    List<Product> getAllAvailableProducts();

    List<Product> searchProducts(String keyword);

    Product getProductById(Long id);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void softDeleteProduct(Long id);

    void updateStock(Long id, int newStock);
}