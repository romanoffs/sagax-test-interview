package com.sagax.shop.controller;

import com.sagax.shop.model.dto.ProductDto;
import com.sagax.shop.model.entity.Product;
import com.sagax.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // CASE 25: Returns 500 instead of 404 for non-existent product.
    // ProductNotFoundException IS handled by GlobalExceptionHandler,
    // but OrderNotFoundException is NOT — inconsistency (Case 28).
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // CASE 25: Returns 200 OK instead of 201 Created for resource creation.
    // CASE 36: Missing @Valid annotation — ProductDto has validation annotations
    // (@NotBlank, @NotNull, @Min) but they are NEVER triggered.
    // Negative prices or null names can be saved.
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        // Should be: @Valid @RequestBody ProductDto productDto
        // Should return: ResponseEntity.status(HttpStatus.CREATED).body(...)
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    // CASE 25: Returns 200 with deleted object body instead of 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(product); // Should be ResponseEntity.noContent().build()
    }

    // CASE 5 (triggered here): getActiveProducts() returns unmodifiable list (toList()),
    // but this endpoint tries to sort it — throws UnsupportedOperationException at runtime.
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        products.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName())); // BOOM!
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<String> purchaseProduct(@PathVariable Long id, @RequestParam int quantity) {
        productService.purchaseProduct(id, quantity);
        return ResponseEntity.ok("Purchase successful");
    }
}
