package com.sagax.shop.util;

import com.sagax.shop.exception.ExerciseNotCompletedException;
import com.sagax.shop.model.entity.Product;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link ProductUtils} is an exercise class focused on {@link Product} domain operations.
 * Each method must be implemented using the Optional API.
 * Every unimplemented method throws {@link ExerciseNotCompletedException}.
 * <p>
 * TODO: remove the exception and implement each method using Optional API
 */
public class ProductUtils {

    /**
     * Wraps a potentially-null {@link Product} in an {@link Optional}.
     *
     * @param product nullable product
     * @return Optional containing the product, or empty if null
     */
    // CASE 44: Optional Creation: ofNullable vs of
    public static Optional<Product> optionalOfProduct(Product product) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns the name of the category associated with the given product.
     * If the product has no category, returns {@code "Uncategorized"}.
     *
     * @param product the product to inspect (never null itself)
     * @return category name or "Uncategorized"
     */
    // CASE 45: Optional mapping chain
    public static String getCategoryName(Product product) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Product} from the supplier.
     * Falls back to {@code defaultProduct} when the supplier supplies nothing.
     *
     * @param productSupplier source of an optional product
     * @param defaultProduct  fallback value
     * @return provided or default product
     */
    // CASE 46: Optional orElse fallback
    public static Product getProductOrDefault(Supplier<Optional<Product>> productSupplier, Product defaultProduct) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns an {@link Optional} containing the product from the supplier only if it is
     * in stock (i.e. {@code stockQuantity > 0}). Otherwise returns {@link Optional#empty()}.
     *
     * @param productSupplier source of an optional product
     * @return Optional with in-stock product, or empty
     */
    // CASE 47: Optional filter
    public static Optional<Product> filterInStock(Supplier<Optional<Product>> productSupplier) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Passes the product to {@code onPresent} when the supplier provides one.
     * Otherwise calls {@code onEmpty}.
     *
     * @param productSupplier source of an optional product
     * @param onPresent       consumer invoked with the product when present
     * @param onEmpty         action invoked when no product is provided
     */
    // CASE 48: Optional ifPresentOrElse
    public static void processProduct(Supplier<Optional<Product>> productSupplier,
                                      Consumer<Product> onPresent,
                                      Runnable onEmpty) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Retrieves the {@link BigDecimal} price of a product using null-safe mapping.
     *
     * @param productSupplier source of an optional product
     * @return Optional containing the price, or empty if no product is provided
     */
    // CASE 49: Optional safely mapping a property
    public static Optional<BigDecimal> getProductPrice(Supplier<Optional<Product>> productSupplier) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Product} from the main supplier, falling back to {@code fallbackSupplier}
     * if the main one supplies nothing.
     * Throws {@link NoSuchElementException} when both suppliers supply no product.
     *
     * @param productSupplier  primary source
     * @param fallbackSupplier secondary source
     * @return product from either supplier
     * @throws NoSuchElementException if both suppliers are empty
     */
    // CASE 50: Optional or() / orElseThrow
    public static Product getProductWithFallback(Supplier<Optional<Product>> productSupplier,
                                                 Supplier<Optional<Product>> fallbackSupplier) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns the SKU of the product supplied. If the product has no SKU set,
     * generates one using the product's category name as prefix (e.g. "Electronics-0001").
     * Products with no category use "GEN" as the prefix.
     * Note: the generator must NOT be invoked when a SKU is already present.
     *
     * @param productSupplier source of a product (must be non-empty)
     * @return existing or newly generated SKU
     */
    // CASE 51: Optional orElseGet for lazy evaluation
    public static String getOrGenerateSku(Supplier<Optional<Product>> productSupplier) {
        throw new ExerciseNotCompletedException();
    }

}
