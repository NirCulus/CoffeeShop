CREATE DATABASE IF NOT EXISTS kopi_kepikiran
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE kopi_kepikiran;

CREATE TABLE IF NOT EXISTS ingredients (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    stock DECIMAL(12,3) NOT NULL DEFAULT 0,
    unit VARCHAR(30) NOT NULL,
    minimum_stock DECIMAL(12,3) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    product_type VARCHAR(20) NOT NULL,
    subcategory VARCHAR(40) NOT NULL,
    base_price DECIMAL(14,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS toppings (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    additional_price DECIMAL(14,2) NOT NULL DEFAULT 0,
    ingredient_id VARCHAR(20),
    ingredient_amount DECIMAL(12,3) NOT NULL DEFAULT 0,
    CONSTRAINT fk_toppings_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recipe_ingredients (
    product_id VARCHAR(20) NOT NULL,
    ingredient_id VARCHAR(20) NOT NULL,
    amount DECIMAL(12,3) NOT NULL,
    PRIMARY KEY (product_id, ingredient_id),
    CONSTRAINT fk_recipe_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_recipe_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(40) PRIMARY KEY,
    occurred_at DATETIME NOT NULL,
    cashier_id VARCHAR(40) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    subtotal DECIMAL(14,2) NOT NULL,
    discount_rate DECIMAL(7,4) NOT NULL,
    discount_amount DECIMAL(14,2) NOT NULL,
    tax_rate DECIMAL(7,4) NOT NULL,
    tax_amount DECIMAL(14,2) NOT NULL,
    total DECIMAL(14,2) NOT NULL,
    amount_received DECIMAL(14,2) NOT NULL,
    change_amount DECIMAL(14,2) NOT NULL,
    INDEX idx_transactions_occurred_at (occurred_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transaction_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(40) NOT NULL,
    product_id VARCHAR(40) NOT NULL,
    product_name VARCHAR(160) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(14,2) NOT NULL,
    line_total DECIMAL(14,2) NOT NULL,
    detail TEXT NOT NULL,
    CONSTRAINT fk_transaction_items_transaction
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT IGNORE INTO ingredients (id, name, stock, unit, minimum_stock) VALUES
    ('BB-01', 'Biji Kopi Espresso', 2000, 'gram', 300),
    ('BB-02', 'Fresh Milk', 5000, 'ml', 1000),
    ('BB-03', 'Sirup Gula Cair', 2000, 'ml', 500),
    ('BB-04', 'Bubuk Matcha Premium', 1000, 'gram', 200),
    ('BB-05', 'Paper Cup & Tutup 12oz', 100, 'pcs', 20),
    ('BB-06', 'Daging Steak Sapi', 25, 'potong', 5),
    ('BB-07', 'Mie Kering Porsi', 50, 'porsi', 10),
    ('BB-08', 'Adonan Pancake', 2000, 'gram', 300),
    ('BB-09', 'Daging Ayam Katsu', 30, 'potong', 5),
    ('BB-10', 'Keju Cheddar Slice', 40, 'lembar', 10),
    ('BB-11', 'Kentang Beku (Fries)', 3000, 'gram', 500),
    ('BB-12', 'Saus Tomat', 100, 'sachet', 20),
    ('BB-13', 'Saus Sambal', 100, 'sachet', 20);

INSERT IGNORE INTO products (id, name, product_type, subcategory, base_price) VALUES
    ('MN-01', 'Espresso Single', 'MINUMAN', 'COFFEE', 15000),
    ('MN-02', 'Americano / Long Black', 'MINUMAN', 'COFFEE', 20000),
    ('MN-03', 'Caffe Latte', 'MINUMAN', 'COFFEE', 25000),
    ('MN-04', 'Matcha Latte', 'MINUMAN', 'NON_COFFEE', 26000),
    ('MK-01', 'Beef Steak Kafe', 'MAKANAN', 'MEALS', 55000),
    ('MK-02', 'Mie Goreng Spesial Kafe', 'MAKANAN', 'MEALS', 22000),
    ('MK-03', 'Crispy Chicken Katsu', 'MAKANAN', 'MEALS', 28000),
    ('MK-04', 'French Fries Crispy', 'MAKANAN', 'SNACK', 16000),
    ('MK-05', 'Fluffy Pancake Maple', 'MAKANAN', 'DESSERT', 20000);

INSERT IGNORE INTO toppings (id, name, additional_price, ingredient_id, ingredient_amount) VALUES
    ('TOP-01', 'Saus Tomat', 1000, 'BB-12', 1),
    ('TOP-02', 'Saus Sambal', 1000, 'BB-13', 1),
    ('TOP-03', 'Extra Keju Slice', 4000, 'BB-10', 1),
    ('TOP-04', 'Extra Espresso Shot', 5000, 'BB-01', 9);

INSERT IGNORE INTO recipe_ingredients (product_id, ingredient_id, amount) VALUES
    ('MN-01', 'BB-01', 18),
    ('MN-01', 'BB-05', 1),
    ('MN-02', 'BB-01', 18),
    ('MN-02', 'BB-05', 1),
    ('MN-03', 'BB-01', 18),
    ('MN-03', 'BB-02', 150),
    ('MN-03', 'BB-05', 1),
    ('MN-04', 'BB-02', 150),
    ('MN-04', 'BB-04', 25),
    ('MN-04', 'BB-05', 1),
    ('MK-01', 'BB-06', 1),
    ('MK-01', 'BB-11', 100),
    ('MK-02', 'BB-07', 1),
    ('MK-03', 'BB-09', 1),
    ('MK-04', 'BB-11', 150),
    ('MK-05', 'BB-08', 150);
