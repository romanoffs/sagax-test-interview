package com.sagax.shop.util;

import com.sagax.shop.model.entity.Category;
import com.sagax.shop.model.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import solutions.ProductUtilsSolution;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ProductUtilsTest {

    @Test
    void optionalOfProduct_nullReturnsEmpty() {
        assertTrue(ProductUtilsSolution.optionalOfProduct(null).isEmpty());
    }

    @Test
    void optionalOfProduct_nonNullReturnsPresent() {
        Product product = productWithPrice("49.99");
        Optional<Product> result = ProductUtilsSolution.optionalOfProduct(product);
        assertTrue(result.isPresent());
        assertSame(product, result.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Electronics", "Books", "Clothing", "Sports"})
    void getCategoryName_returnsCategoryNameWhenPresent(String categoryName) {
        Product product = productWithCategory(categoryName);
        assertEquals(categoryName, ProductUtilsSolution.getCategoryName(product));
    }

    @Test
    void getCategoryName_returnsUncategorizedWhenCategoryIsNull() {
        Product product = productWithPrice("10.00");
        assertEquals("Uncategorized", ProductUtilsSolution.getCategoryName(product));
    }

    @Test
    void getProductOrDefault_returnsDefaultWhenSupplierIsEmpty() {
        Product defaultProduct = productWithPrice("0.00");
        Product result = ProductUtilsSolution.getProductOrDefault(Optional::empty, defaultProduct);
        assertSame(defaultProduct, result);
    }

    @Test
    void getProductOrDefault_returnsProvidedProductWhenPresent() {
        Product provided = productWithPrice("99.99");
        Product result = ProductUtilsSolution.getProductOrDefault(() -> Optional.of(provided), productWithPrice("0.00"));
        assertSame(provided, result);
    }

    @Test
    void filterInStock_returnsProductWhenInStock() {
        Product product = productWithStock(5);
        assertTrue(ProductUtilsSolution.filterInStock(() -> Optional.of(product)).isPresent());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void filterInStock_returnsEmptyWhenOutOfStock(int stock) {
        Product product = productWithStock(stock);
        assertTrue(ProductUtilsSolution.filterInStock(() -> Optional.of(product)).isEmpty());
    }

    @Test
    void processProduct_callsOnPresentWhenProductExists() {
        AtomicBoolean processed = new AtomicBoolean(false);
        Product product = productWithPrice("25.00");

        ProductUtilsSolution.processProduct(
                () -> Optional.of(product),
                p -> processed.set(true),
                () -> fail("onEmpty should not be called")
        );

        assertTrue(processed.get());
    }

    @Test
    void processProduct_callsOnEmptyWhenNoProduct() {
        AtomicBoolean noProductCalled = new AtomicBoolean(false);

        ProductUtilsSolution.processProduct(
                Optional::empty,
                p -> fail("onPresent should not be called"),
                () -> noProductCalled.set(true)
        );

        assertTrue(noProductCalled.get());
    }

    @Test
    void getProductPrice_returnsPriceWhenProductPresent() {
        BigDecimal price = new BigDecimal("149.99");
        Product product = productWithPrice(price.toPlainString());
        Optional<BigDecimal> result = ProductUtilsSolution.getProductPrice(() -> Optional.of(product));
        assertTrue(result.isPresent());
        assertEquals(price, result.get());
    }

    @Test
    void getProductPrice_returnsEmptyWhenSupplierIsEmpty() {
        assertTrue(ProductUtilsSolution.getProductPrice(Optional::empty).isEmpty());
    }

    @Test
    void getProductWithFallback_returnsFallbackWhenMainSupplierIsEmpty() {
        Product fallback = productWithPrice("5.00");
        Product result = ProductUtilsSolution.getProductWithFallback(Optional::empty, () -> Optional.of(fallback));
        assertSame(fallback, result);
    }

    @Test
    void getProductWithFallback_throwsWhenBothSuppliersAreEmpty() {
        assertThrows(NoSuchElementException.class,
                () -> ProductUtilsSolution.getProductWithFallback(Optional::empty, Optional::empty));
    }

    @Test
    void getOrGenerateSku_returnsExistingSkuWithoutGenerating() {
        Product product = productWithPrice("30.00");
        product.setSku("EXISTING-0001");
        String sku = ProductUtilsSolution.getOrGenerateSku(() -> Optional.of(product));
        assertEquals("EXISTING-0001", sku);
    }

    @Test
    void getOrGenerateSku_generatesSkuWithCategoryPrefixWhenSkuIsAbsent() {
        Product product = productWithCategory("Electronics");
        product.setSku(null);
        String sku = ProductUtilsSolution.getOrGenerateSku(() -> Optional.of(product));
        assertTrue(sku.startsWith("Electronics-"), "Generated SKU should start with category prefix");
    }

    @Test
    void getOrGenerateSku_usesFallbackPrefixWhenCategoryIsNull() {
        Product product = productWithPrice("15.00");
        product.setSku(null);
        String sku = ProductUtilsSolution.getOrGenerateSku(() -> Optional.of(product));
        assertTrue(sku.startsWith("GEN-"), "Generated SKU should start with GEN when no category");
    }

    private Product productWithPrice(String price) {
        Product product = new Product();
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(10);
        return product;
    }

    private Product productWithCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        product.setCategory(category);
        product.setStockQuantity(10);
        return product;
    }

    private Product productWithStock(int stockQuantity) {
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        product.setStockQuantity(stockQuantity);
        return product;
    }
}
