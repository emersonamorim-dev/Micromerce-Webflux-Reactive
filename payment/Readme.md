### Micromerce Payment Service - Sistema de Pagamento Reativo 💳 🚀 🌐

Codificação em Java 21 com Spring WebFlux, MySQL, Redis, Kafka e Elasticsearch e Kiabana para aplicação de microserviços. O Micromerce Payment Service é uma implementação moderna e inovadora de um microserviço de pagamento, desenvolvido com as mais recentes tecnologias reativas e práticas de arquitetura de software. Este projeto demonstra a construção de um sistema altamente escalável e resiliente, utilizando Spring WebFlux para programação reativa, MySQL para persistência, Redis para cache distribuído, Kafka para mensageria assíncrona e Elasticsearch para busca avançada.

A arquitetura do serviço foi cuidadosamente projetada seguindo os princípios da arquitetura Hexagonal (ports and adapters), garantindo alta coesão e baixo acoplamento. O sistema é totalmente containerizado com Docker, facilitando a implantação e escalabilidade, além de implementar circuit breakers com Resilience4j para maior resiliência em ambientes distribuídos.

#### Tecnologias Utilizadas

- **Java 21**: Última versão LTS do Java com recursos modernos
- **Spring Boot 3.2.0**: Framework base com suporte a programação reativa
- **Spring WebFlux**: Stack reativa para alta performance e escalabilidade
- **MySQL**: Banco de dados principal (SQL)
- **Redis**: Cache distribuído para alta performance
- **Apache Kafka**: Mensageria para eventos assíncronos
- **Elasticsearch**: Busca e análise de dados
- **Docker & Docker Compose**: Containerização e orquestração
- **Spring Security**: Segurança e autenticação
- **Resilience4j**: Circuit breaker para resiliência
- **Kibana**: Métricas e monitoramento

#### Tecnologias Utilizadas

![Java 21](https://img.shields.io/badge/Java-21.0.1-%23ED8B00?logo=openjdk&logoColor=white)  
![Spring Boot 3.2.0](https://img.shields.io/badge/Spring%20Boot-3.2.0-%236DB33F?logo=springboot&logoColor=white)  
![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-%236DB33F?logo=springboot&logoColor=white)  
![MySQL](https://img.shields.io/badge/MySQL-Database-%234479A1?logo=mysql&logoColor=white)  
![Redis](https://img.shields.io/badge/Redis-Cache-%23DC382D?logo=redis&logoColor=white)  
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Messaging-%230D3646?logo=apachekafka&logoColor=white)  
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-Search%20%26%20Analytics-%23005571?logo=elasticsearch&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-Containerization-%232496ED?logo=docker&logoColor=white)  
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Orchestration-%232496ED?logo=docker&logoColor=white)  
![Spring Security](https://img.shields.io/badge/Spring%20Security-Authentication-%236DB33F?logo=springsecurity&logoColor=white)  
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-%23D62B00?logo=java&logoColor=white)  
![Kibana](https://img.shields.io/badge/Kibana-Metrics%20%26%20Monitoring-%23005571?logo=kibana&logoColor=white)  

#### Arquitetura

A Arquitetura segue o padrão de Clean Architecture e Arquitetura hexagonal (ports and adapters) com as seguintes camadas:

### Componentes Principais:
- **API Layer**: Controllers reativos
- **Application Layer**: Use cases e DTOs
- **Domain Layer**: Modelos e regras de negócio
- **Infrastructure Layer**: Implementações técnicas

#### Como Executar

#### Pré-requisitos
- Docker
- Docker Compose
- Java 21 (para desenvolvimento)

#### Passos para Execução


1. Clone o repositório:
```
git clone [repository-url]
cd Micromerce-Webflux-Reactive/cart
```

2. Execute com Docker Compose:
```bash
docker build -t payment-microservice:latest .
```

```bash
docker-compose up -d
```

3. Verifique os serviços:
```bash
docker-compose ps
```

mvn dependency:purge-local-repository


./kafka-cleanup.bat

cmd.exe /c kafka-cleanup.bat


#### Endpoints da API
1. Criar Pagamento

- POST http://localhost:8083/api/v1/payments
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para Cartão de Crédito:
```
{
    "paymentType": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "cardHolderName": "Emerson Amorim",
    "cvv": "123",
    "amount": 1800.00,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123"
}
```

- Resposta:
```
{
    "id": "1ee1463f-23fb-45bd-9be8-235e12a21011",
    "paymentType": "CREDIT_CARD",
    "cardNumber": "**** **** **** 1111",
    "cardHolderName": "Emerson Amorim",
    "cvv": "123",
    "amount": 1800.0,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123",
    "status": "PROCESSING",
    "createdAt": "2024-12-15T08:20:40.018625888"
}
```

2. Criar Pagamento

- POST http://localhost:8083/api/v1/payments
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para Cartão de Débito:

```
{
    "paymentType": "DEBIT_CARD",
    "cardNumber": "4111111111111111",
    "cardHolderName": "Emerson Amorim",
    "cvv": "123",
    "amount": 180.00,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123"
}
```

- Resposta:
```
{
    "id": "8d63a352-6111-4770-a9a1-df9ca0e633a2",
    "paymentType": "DEBIT_CARD",
    "cardNumber": "**** **** **** 1111",
    "cardHolderName": "Emerson Amorim",
    "amount": 180.0,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123",
    "status": "PROCESSING",
    "createdAt": "2024-12-15T08:21:43.718818747"
}
```

3. Criar Pagamento

- POST http://localhost:8083/api/v1/payments
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para Cartão de Boleto:

```
{
"paymentType": "BOLETO",
"boletoNumber": "34191790010104351004791020150008291070026000",
"amount": 180.00,
"beneficiario": "Emerson Amorim",
"dueDate": "2024-12-31T23:59:59",
"customerId": "550e8400-e29b-41d4-a716-446655440000",
"pagador": "Emerson Luiz",
"orderId": "order123"
}
```

- Resposta:
```
{
    "id": "01395811-d3f4-4bb8-812c-de84210396cb",
    "paymentType": "BOLETO",
    "boletoNumber": "34191790010104351004791020150008291070026000",
    "dueDate": "2024-12-31T23:59:59",
    "amount": 180.0,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123",
    "status": "PROCESSING",
    "createdAt": "2024-12-15T08:23:46.159485185"
}
```

4. Criar Pagamento

- POST http://localhost:8083/api/v1/payments
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para Pix:

```
{
"paymentType": "PIX",
"pixKey": "emerson_tecno@hotmail.com",
"pixKeyType": "EMAIL",
"amount": 180.00,
"customerId": "550e8400-e29b-41d4-a716-446655440000",
"orderId": "order123"
}
```

- Resposta:
```
{
    "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
    "paymentType": "PIX",
    "pixKey": "emerson_tecno@hotmail.com",
    "pixKeyType": "EMAIL",
    "amount": 180.0,
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "orderId": "order123",
    "status": "PROCESSING",
    "createdAt": "2024-12-15T08:25:18.863994223"
}
```

5. Listar Pagamento Processados

- Get http://localhost:8083/api/v1/payments
- Paginação: http://localhost:8083/api/v1/payments?page=2&size=1
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para Retorno de Pagamento Processados:

```
{
    "content": [
        {
            "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
            "amount": 180.00,
            "status": "PROCESSING",
            "paymentIdentifier": "emerson_tecno@hotmail.com"
        },
        {
            "id": "01395811-d3f4-4bb8-812c-de84210396cb",
            "amount": 180.00,
            "beneficiario": "Emerson Amorim",
            "status": "PROCESSING",
            "pagador": "Emerson Luiz",
            "paymentIdentifier": "34191790010104351004791020150008291070026000"
        },
        {
            "id": "8d63a352-6111-4770-a9a1-df9ca0e633a2",
            "amount": 180.00,
            "status": "PROCESSING",
            "paymentIdentifier": "4111111111111111"
        },
        {
            "id": "1ee1463f-23fb-45bd-9be8-235e12a21011",
            "amount": 1800.00,
            "status": "PROCESSING",
            "paymentIdentifier": "4111111111111111"
        },
        {
            "id": "f3a9621e-23a4-44ac-8290-3317b9f2719e",
            "amount": 180.00,
            "status": "PROCESSING",
            "paymentIdentifier": "emerson_tecno@hotmail.com"
        },
        {
            "id": "c176497a-dfc4-4e53-977a-dcda8943352f",
            "amount": 180.00,
            "beneficiario": "Emerson Amorim",
            "status": "PROCESSING",
            "pagador": "Emerson Luiz",
            "paymentIdentifier": "34191790010104351004791020150008291070026000"
        },
        {
            "id": "56bc6652-d2a0-4ebe-9669-ce72a44990fe",
            "amount": 180.00,
            "status": "PROCESSING",
            "paymentIdentifier": "4111111111111111"
        },
        {
            "id": "319d3929-b5dc-4268-9feb-c3169df08925",
            "amount": 1800.00,
            "status": "PROCESSING",
            "paymentIdentifier": "4111111111111111"
        }
    ],
    "pageInfo": {
        "pageNumber": 0,
        "pageSize": 20,
        "totalElements": 8,
        "totalPages": 1,
        "hasNext": false,
        "hasPrevious": false,
        "first": true,
        "last": true
    }
}
```

6. Requisição Reembolso de Pagamento

- Post http://localhost:8083/api/v1/payments/2285dad8-cf53-4ae2-891a-4e627d11c5e6/refund
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Retorno da Requisição para Reembolso de Pagamento:

```
{
    "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
    "amount": 180.00,
    "status": "REFUNDED"
}
```

7. Requisição Cancelar de Pagamento

- Post http://localhost:8083/api/v1/payments/01395811-d3f4-4bb8-812c-de84210396cb/cancel
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Retorno da Requisição para Cancelar de Pagamento:

```
{
    "id": "01395811-d3f4-4bb8-812c-de84210396cb",
    "amount": 180.00,
    "beneficiario": "Emerson Amorim",
    "status": "CANCELLED",
    "pagador": "Emerson Luiz"
}
```

8. Requisição Pagamento por ID

- Get http://localhost:8083/api/v1/payments/2285dad8-cf53-4ae2-891a-4e627d11c5e6
  Content-Type: application/json
  Authorization: Basic Auth:  admin admin

- Retorno da Requisição para Cancelar de Pagamento:

```
{
    "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
    "amount": 180.00,
    "status": "PROCESSING"
}
```


9. Requisição Pedido por ID

- Get http://localhost:8083/api/v1/payments/order/order123
  Content-Type: application/json
  Authorization: Basic Auth:  admin admin

- Retorno da Requisição para Cancelar de Pagamento:

```
[
    {
        "id": "01395811-d3f4-4bb8-812c-de84210396cb",
        "amount": 180.00,
        "beneficiario": "Emerson Amorim",
        "createdAt": "2024-12-15T08:23:46",
        "status": "PROCESSING",
        "pagador": "Emerson Luiz",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "BOLETO"
    },
    {
        "id": "1ee1463f-23fb-45bd-9be8-235e12a21011",
        "amount": 1800.00,
        "createdAt": "2024-12-15T08:20:40",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "CREDIT_CARD"
    },
    {
        "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:25:19",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "PIX"
    },
    {
        "id": "319d3929-b5dc-4268-9feb-c3169df08925",
        "amount": 1800.00,
        "createdAt": "2024-12-15T07:58:36",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "CREDIT_CARD"
    },
    {
        "id": "56bc6652-d2a0-4ebe-9669-ce72a44990fe",
        "amount": 180.00,
        "createdAt": "2024-12-15T07:59:37",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "DEBIT_CARD"
    },
    {
        "id": "8d63a352-6111-4770-a9a1-df9ca0e633a2",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:21:44",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "DEBIT_CARD"
    },
    {
        "id": "c176497a-dfc4-4e53-977a-dcda8943352f",
        "amount": 180.00,
        "beneficiario": "Emerson Amorim",
        "createdAt": "2024-12-15T08:00:04",
        "status": "PROCESSING",
        "pagador": "Emerson Luiz",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "BOLETO"
    },
    {
        "id": "f3a9621e-23a4-44ac-8290-3317b9f2719e",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:00:44",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "PIX"
    }
]
```

10. Requisição Cliente por ID

- Get http://localhost:8083/api/v1/payments/customer/550e8400-e29b-41d4-a716-446655440000
  Content-Type: application/json
  Authorization: Basic Auth:  admin admin

- Retorno da Requisição para Cancelar de Pagamento:

```
[
    {
        "id": "01395811-d3f4-4bb8-812c-de84210396cb",
        "amount": 180.00,
        "beneficiario": "Emerson Amorim",
        "createdAt": "2024-12-15T08:23:46",
        "status": "PROCESSING",
        "pagador": "Emerson Luiz",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "BOLETO"
    },
    {
        "id": "1ee1463f-23fb-45bd-9be8-235e12a21011",
        "amount": 1800.00,
        "createdAt": "2024-12-15T08:20:40",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "CREDIT_CARD"
    },
    {
        "id": "2285dad8-cf53-4ae2-891a-4e627d11c5e6",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:25:19",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "PIX"
    },
    {
        "id": "319d3929-b5dc-4268-9feb-c3169df08925",
        "amount": 1800.00,
        "createdAt": "2024-12-15T07:58:36",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "CREDIT_CARD"
    },
    {
        "id": "56bc6652-d2a0-4ebe-9669-ce72a44990fe",
        "amount": 180.00,
        "createdAt": "2024-12-15T07:59:37",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "DEBIT_CARD"
    },
    {
        "id": "8d63a352-6111-4770-a9a1-df9ca0e633a2",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:21:44",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "DEBIT_CARD"
    },
    {
        "id": "c176497a-dfc4-4e53-977a-dcda8943352f",
        "amount": 180.00,
        "beneficiario": "Emerson Amorim",
        "createdAt": "2024-12-15T08:00:04",
        "status": "PROCESSING",
        "pagador": "Emerson Luiz",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "BOLETO"
    },
    {
        "id": "f3a9621e-23a4-44ac-8290-3317b9f2719e",
        "amount": 180.00,
        "createdAt": "2024-12-15T08:00:44",
        "status": "PROCESSING",
        "customerId": "550e8400-e29b-41d4-a716-446655440000",
        "orderId": "order123",
        "paymentType": "PIX"
    }
]
```

- Para acessar o container MySQL identificado pelo ID cd06d33fe1a2, use o seguinte comando:

1. Entrar no container:

```
   docker exec -it cd06d33fe1a2 bash - id do seu container
```   

2. Acessar o MySQL dentro do container:
   Após entrar no container, use o cliente MySQL para acessar o banco de dados:

```
mysql -u root -p
```

Substitua root pelo nome de usuário configurado para o MySQL, se necessário. Você será solicitado a fornecer a senha configurada para o MySQL.

3. Verificar tabelas no banco payment_db:
   Após entrar no MySQL, selecione o banco de dados e liste as tabelas:

```
USE payment_db;
```

```
SHOW TABLES;
```
Se precisar de ajuda adicional, é só perguntar! 😊


Ver o que temos nessa coluna do banco de dados
+----------------------+
| Tables_in_payment_db |
+----------------------+


4. Obter o esquema da tabela payment:
   Veja todas as colunas da tabela com o seguinte comando:

```
DESCRIBE payment;
```

Esse comando listará as colunas disponíveis, seus tipos de dados e outras informações.

5. Consultar os dados de uma coluna específica:
   Para ver os dados de uma coluna (por exemplo, column_name), execute:

```
SELECT column_name FROM payment;
```
Substitua column_name pelo nome da coluna que você deseja visualizar.

6. Ver todas as colunas e algumas linhas da tabela:
   Se você quiser visualizar todas as colunas da tabela payment, mas apenas uma amostra dos dados:

```
SELECT * FROM payment LIMIT 10;
```
Isso exibirá as 10 primeiras linhas da tabela.



#### Fluxo da Aplicação

- Criação do Pagamento:
- Usuário cria um pagamento via endpoint /payments
- Sistema gera ID único e persiste no MySQL
- Evento de criação é publicado no Kafka
- Consulta de Pagamento:
- Detalhes do pagamento podem ser consultados via endpoint /payments/{paymentId}
- Atualização de Status:
- O status do pagamento pode ser atualizado conforme o processamento
- Monitoramento
- Métricas: Disponíveis via Actuator em /actuator/metrics
- Health Check: Status em /actuator/health
- Prometheus: Métricas em /actuator/prometheus


### Conclusão
O Micromerce Payment Service representa uma implementação state-of-the-art de um microserviço de pagamento, demonstrando como tecnologias modernas e práticas avançadas de arquitetura podem ser combinadas para criar um sistema robusto e escalável.

A escolha do Spring WebFlux como base da aplicação permite o processamento assíncrono e não-bloqueante, resultando em melhor utilização de recursos e maior capacidade de lidar com cargas concorrentes. A integração com MySQL, Redis e Kafka cria um ecossistema distribuído resiliente, enquanto o Elasticsearch fornece capacidades avançadas de busca e análise.

O projeto segue rigorosamente os princípios SOLID e Clean Architecture, resultando em um código altamente manutenível e testável. O uso de padrões de projeto como Repository, Factory e Builder simplifica a complexidade do código e melhora sua organização.

A containerização com Docker e a orquestração com Docker Compose garantem que o serviço possa ser facilmente implantado em qualquer ambiente, enquanto as métricas do Prometheus e os endpoints do Actuator fornecem observabilidade completa do sistema em produção.

Este microserviço serve como um exemplo prático de como construir sistemas modernos e escaláveis utilizando tecnologias reativas, demonstrando boas práticas de desenvolvimento e arquitetura que podem ser aplicadas em projetos empresariais de larga escala.

### Desenvolvido por:
Emerson Amorim [@emerson-amorim-dev](https://www.linkedin.com/in/emerson-amorim-dev/)

