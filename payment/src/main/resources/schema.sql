-- Drop tables if they exist
DROP TABLE IF EXISTS payment_details;
DROP TABLE IF EXISTS payment_transaction;
DROP TABLE IF EXISTS payment;

-- Create payment table
CREATE TABLE payment (
    id VARCHAR(36) PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    
    -- Campos para cartão (crédito e débito)
    card_number VARCHAR(255),
    card_holder_name VARCHAR(255),
    cvv VARCHAR(4),
    
    -- Campos para boleto
    boleto_number VARCHAR(255),
    beneficiario VARCHAR(255),
    pagador VARCHAR(255),
    due_date TIMESTAMP,
    
    -- Campos para PIX
    pix_key VARCHAR(255),
    pix_key_type VARCHAR(20),
    
    -- Constraints para validação
    CONSTRAINT chk_payment_type CHECK (payment_type IN ('CREDIT_CARD', 'DEBIT_CARD', 'BOLETO', 'PIX')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    
    -- Validações específicas por tipo de pagamento
    CONSTRAINT chk_credit_card CHECK (
        payment_type != 'CREDIT_CARD' OR 
        (card_number IS NOT NULL AND card_holder_name IS NOT NULL AND cvv IS NOT NULL)
    ),
    CONSTRAINT chk_debit_card CHECK (
        payment_type != 'DEBIT_CARD' OR 
        (card_number IS NOT NULL AND card_holder_name IS NOT NULL)
    ),
    CONSTRAINT chk_boleto CHECK (
        payment_type != 'BOLETO' OR 
        (boleto_number IS NOT NULL AND due_date IS NOT NULL)
    ),
    CONSTRAINT chk_pix CHECK (
        payment_type != 'PIX' OR 
        (pix_key IS NOT NULL AND pix_key_type IS NOT NULL)
    ),
    
    -- Índices para otimização de consultas
    INDEX idx_customer_id (customer_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_payment_type (payment_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_transaction table
CREATE TABLE payment_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL,
    transaction_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,
    
    CONSTRAINT fk_payment_transaction 
        FOREIGN KEY (payment_id) 
        REFERENCES payment(id) 
        ON DELETE CASCADE,
        
    CONSTRAINT chk_transaction_status CHECK (
        status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED')
    ),
    
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_id (payment_id),
    INDEX idx_transaction_status (status),
    INDEX idx_transaction_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_details table
CREATE TABLE payment_details (
    id VARCHAR(255) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(50),
    error_message TEXT,
    transaction_id VARCHAR(255),
    customer_id VARCHAR(255),
    metadata TEXT,
    payment_id VARCHAR(36),
    version BIGINT DEFAULT 0,
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    
    FOREIGN KEY (payment_id) REFERENCES payment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;