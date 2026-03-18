CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    password   VARCHAR(255),
    role       VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE address
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT,
    city     VARCHAR(255),
    street   VARCHAR(255),
    zip_code VARCHAR(20),
    country  VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE product
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    sku            VARCHAR(100) UNIQUE,
    price          DECIMAL(19, 2),
    stock_quantity INT    DEFAULT 0,
    description    TEXT,
    category_id    BIGINT,
    version        BIGINT DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT,
    status       INT,
    total_amount DECIMAL(19, 2),
    version      BIGINT DEFAULT 0,
    created_at   TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE order_item
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id          BIGINT,
    product_id        BIGINT,
    quantity          INT,
    price_at_purchase DECIMAL(19, 2),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE payment
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT UNIQUE,
    amount         DECIMAL(19, 2),
    status         VARCHAR(50),
    transaction_id VARCHAR(255),
    processed_at   TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
