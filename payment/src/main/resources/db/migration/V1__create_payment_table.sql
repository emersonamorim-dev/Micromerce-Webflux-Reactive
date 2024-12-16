CREATE TABLE payment (
    id VARCHAR(36) PRIMARY KEY,
    payment_type VARCHAR(20) NOT NULL,
    amount DECIMAL(20,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    status VARCHAR(20) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    payment_method_id VARCHAR(36),
    error_code VARCHAR(50),
    error_message TEXT,
    
    INDEX idx_payment_status (status),
    INDEX idx_payment_customer (customer_id),
    INDEX idx_payment_order (order_id),
    INDEX idx_payment_created (created_at),
    INDEX idx_payment_type (payment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payment_credit_card (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL,
    card_number_hash VARCHAR(255) NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    card_brand VARCHAR(50),
    card_last_digits VARCHAR(4) NOT NULL,
    installments INT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (payment_id) REFERENCES payment(id),
    INDEX idx_cc_payment (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payment_pix (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL,
    pix_key VARCHAR(255) NOT NULL,
    pix_key_type VARCHAR(20) NOT NULL,
    qr_code TEXT,
    qr_code_url VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (payment_id) REFERENCES payment(id),
    INDEX idx_pix_payment (payment_id),
    INDEX idx_pix_key (pix_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payment_boleto (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL,
    boleto_number VARCHAR(255) NOT NULL,
    barcode VARCHAR(255),
    due_date TIMESTAMP NOT NULL,
    paid_at TIMESTAMP,
    
    FOREIGN KEY (payment_id) REFERENCES payment(id),
    INDEX idx_boleto_payment (payment_id),
    INDEX idx_boleto_number (boleto_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
