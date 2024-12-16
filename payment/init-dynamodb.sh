#!/bin/bash

echo "Waiting for MySQL to start..."
sleep 10  # Aguarde o MySQL inicializar (ajuste o tempo conforme necessário)

echo "Creating 'paymentDB' table in MySQL..."

# Execute o comando para criar a tabela diretamente no MySQL
mysql -h 127.0.0.1 -P 3306 -u root -prootpassword <<EOF
CREATE DATABASE IF NOT EXISTS micromerce;

USE micromerce;

CREATE TABLE IF NOT EXISTS paymentDB (
    id CHAR(36) NOT NULL PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    beneficiario VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PROCESSING', 'COMPLETED', 'FAILED') NOT NULL,
    pagador VARCHAR(255),
    customer_id CHAR(36),
    order_id VARCHAR(255),
    payment_type ENUM('CREDIT_CARD', 'PIX', 'BOLETO') NOT NULL,
    card_number VARCHAR(16),
    card_holder_name VARCHAR(255),
    cvv VARCHAR(4),
    boleto_number VARCHAR(255),
    due_date DATE,
    pix_key VARCHAR(255),
    pix_key_type ENUM('EMAIL', 'PHONE', 'CPF', 'CNPJ')
);

echo "MySQL table created successfully."
EOF
