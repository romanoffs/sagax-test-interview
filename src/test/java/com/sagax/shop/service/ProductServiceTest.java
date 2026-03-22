package com.sagax.shop.service;

import com.sagax.shop.model.dto.ProductDto;
import com.sagax.shop.model.entity.Product;
import com.sagax.shop.repository.CategoryRepository;
import com.sagax.shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct() {
        ProductDto dto = new ProductDto();
        dto.setName("Test Product");
        dto.setPrice(new BigDecimal("29.99"));
        dto.setStockQuantity(10);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Test Product");
        saved.setPrice(new BigDecimal("29.99"));

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.createProduct(dto);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, never()).findById(any());

        assertEquals("Test Product", result.getName());
    }

    @Test
    void testGetProductById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        verify(productRepository).findById(1L);
        assertNotNull(result);
    }
}
