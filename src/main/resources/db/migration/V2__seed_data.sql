-- Seed categories
INSERT INTO category (name)
VALUES ('Electronics');
INSERT INTO category (name)
VALUES ('Books');
INSERT INTO category (name)
VALUES ('Clothing');

-- Seed users (CASE 27: plain text passwords stored)
INSERT INTO users (email, first_name, last_name, password, role)
VALUES ('admin@shop.com', 'Admin', 'User', 'admin123', 'ADMIN');
INSERT INTO users (email, first_name, last_name, password, role)
VALUES ('john@example.com', 'John', 'Doe', 'password123', 'USER');
INSERT INTO users (email, first_name, last_name, password, role)
VALUES ('jane@example.com', 'Jane', 'Smith', 'qwerty', 'USER');

-- Seed products
INSERT INTO product (name, sku, price, stock_quantity, description, category_id, version, created_at, updated_at)
VALUES ('Laptop Pro 15', 'ELEC-001', 1299.99, 50, 'High-performance laptop', 1, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO product (name, sku, price, stock_quantity, description, category_id, version, created_at, updated_at)
VALUES ('Wireless Mouse', 'ELEC-002', 29.99, 200, 'Ergonomic wireless mouse', 1, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO product (name, sku, price, stock_quantity, description, category_id, version, created_at, updated_at)
VALUES ('Java in Action', 'BOOK-001', 49.90, 100, 'Comprehensive Java guide', 2, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO product (name, sku, price, stock_quantity, description, category_id, version, created_at, updated_at)
VALUES ('Spring Boot Guide', 'BOOK-002', 39.90, 75, 'Spring Boot reference', 2, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO product (name, sku, price, stock_quantity, description, category_id, version, created_at, updated_at)
VALUES ('Cotton T-Shirt', 'CLOTH-001', 19.99, 300, 'Basic cotton t-shirt', 3, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CASE 21: Seed data references column 'phone' which does NOT exist in V1 migration
-- This line will cause Flyway migration to fail if uncommented:
-- INSERT INTO users (email, first_name, last_name, password, phone) VALUES ('test@test.com', 'Test', 'User', 'test', '+380501234567');

-- Seed orders (status as ordinal: 0=PENDING, 1=CONFIRMED, 2=SHIPPED)
INSERT INTO orders (user_id, status, total_amount, version, created_at)
VALUES (2, 0, 1329.98, 0, CURRENT_TIMESTAMP);
INSERT INTO orders (user_id, status, total_amount, version, created_at)
VALUES (3, 1, 49.90, 0, CURRENT_TIMESTAMP);

-- Seed order items
INSERT INTO order_item (order_id, product_id, quantity, price_at_purchase)
VALUES (1, 1, 1, 1299.99);
INSERT INTO order_item (order_id, product_id, quantity, price_at_purchase)
VALUES (1, 2, 1, 29.99);
INSERT INTO order_item (order_id, product_id, quantity, price_at_purchase)
VALUES (2, 3, 1, 49.90);

-- Seed payments
INSERT INTO payment (order_id, amount, status, transaction_id, processed_at)
VALUES (1, 1329.98, 'COMPLETED', 'TXN-001', CURRENT_TIMESTAMP);
