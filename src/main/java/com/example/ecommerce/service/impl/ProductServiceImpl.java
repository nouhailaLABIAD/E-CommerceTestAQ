package com.example.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.interfaces.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllAvailableProducts() {
        return productRepository.findByDeletedFalse();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository
                .findByNomContainingIgnoreCaseAndDeletedFalse(keyword);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = getProductById(id);
        product.setNom(updatedProduct.getNom());
        product.setPrix(updatedProduct.getPrix());
        product.setStock(updatedProduct.getStock());
        product.setDescription(updatedProduct.getDescription());
        return productRepository.save(product);
    }

    @Override
    public void softDeleteProduct(Long id) {
        Product product = getProductById(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public void updateStock(Long id, int newStock) {
        Product product = getProductById(id);
        product.setStock(newStock);
        productRepository.save(product);
    }
}