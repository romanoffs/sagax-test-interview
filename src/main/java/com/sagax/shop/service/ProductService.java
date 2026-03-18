package com.sagax.shop.service;

import com.sagax.shop.exception.InsufficientStockException;
import com.sagax.shop.exception.ProductNotFoundException;
import com.sagax.shop.model.dto.ProductDto;
import com.sagax.shop.model.entity.Product;
import com.sagax.shop.repository.CategoryRepository;
import com.sagax.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    // CASE 5: toList() returns an unmodifiable list.
    // If a caller tries to sort() or add() to the result, it will throw UnsupportedOperationException.
    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .toList(); // Returns unmodifiable list!
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId())
                    .ifPresent(product::setCategory);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setDescription(dto.getDescription());
        return productRepository.save(product);
    }

    // CASE 37: Race condition — non-atomic read-modify-write.
    // Two concurrent threads can both read stock=10, both set stock=9 instead of 8.
    // CASE 20: The Product entity HAS @Version, but this read-modify-write pattern
    // may still cause lost updates under high concurrency without proper retry logic.
    @Transactional
    public void purchaseProduct(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }

        // TOCTOU: between the check above and the save below,
        // another thread can modify stock
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setDescription(product.getDescription());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }
        return dto;
    }
}
