package com.example.ecommerce.service.impl;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldReturnOnlyNonDeletedProducts() {

        Product p = new Product();
        p.setDeleted(false);

        when(productRepository.findByDeletedFalse())
                .thenReturn(List.of(p));

        List<Product> result = productService.getAllAvailableProducts();

        assertEquals(1, result.size());
    }
    @Test
void shouldSoftDeleteProduct() {

    Product product = new Product();
    product.setDeleted(false);

    when(productRepository.findById(1L))
            .thenReturn(java.util.Optional.of(product));

    productService.softDeleteProduct(1L);

    assertEquals(true, product.isDeleted());
}
@Test
void shouldSearchProducts() {

    when(productRepository
            .findByNomContainingIgnoreCaseAndDeletedFalse("phone"))
            .thenReturn(List.of(new Product()));

    List<Product> result = productService.searchProducts("phone");

    assertEquals(1, result.size());
}
@Test
void shouldUpdateStock() {

    Product product = new Product();
    product.setStock(5);

    when(productRepository.findById(1L))
            .thenReturn(java.util.Optional.of(product));

    productService.updateStock(1L, 10);

    assertEquals(10, product.getStock());
}
}