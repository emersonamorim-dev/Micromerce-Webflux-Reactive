### Micromerce Cart Service - Sistema de Carrinho Reativo 🚀 🔄 🌐

O Micromerce Cart Service é uma implementação moderna e inovadora de um microserviço de carrinho de compras, desenvolvido com as mais recentes tecnologias reativas e práticas de arquitetura de software. Este projeto demonstra a construção de um sistema altamente escalável e resiliente, utilizando Spring WebFlux para programação reativa, MongoDB para persistência, Redis para cache distribuído, Kafka para mensageria assíncrona e Elasticsearch para busca avançada.

A arquitetura do serviço foi cuidadosamente projetada seguindo os princípios da arquitetura hexagonal (ports and adapters), garantindo alta coesão e baixo acoplamento. O sistema é totalmente containerizado com Docker, facilitando a implantação e escalabilidade, além de implementar circuit breakers com Resilience4j para maior resiliência em ambientes distribuídos.


#### Tecnologias Utilizadas

- **Java 21**: Última versão LTS do Java com recursos modernos
- **Spring Boot 3.2.0**: Framework base com suporte a programação reativa
- **Spring WebFlux**: Stack reativa para alta performance e escalabilidade
- **MongoDB**: Banco de dados principal (NoSQL)
- **Redis**: Cache distribuído para alta performance
- **Apache Kafka**: Mensageria para eventos assíncronos
- **Elasticsearch**: Busca e análise de dados
- **Docker & Docker Compose**: Containerização e orquestração
- **Spring Security**: Segurança e autenticação
- **Resilience4j**: Circuit breaker para resiliência
- **Kibana**: Métricas e monitoramento

# Tecnologias Utilizadas

![Java 21](https://img.shields.io/badge/Java-21.0.1-%23ED8B00?logo=openjdk&logoColor=white)  
![Spring Boot 3.2.0](https://img.shields.io/badge/Spring%20Boot-3.2.0-%236DB33F?logo=springboot&logoColor=white)  
![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-%236DB33F?logo=springboot&logoColor=white)  
![MongoDB](https://img.shields.io/badge/MongoDB-Database-%2347A248?logo=mongodb&logoColor=white)  
![Redis](https://img.shields.io/badge/Redis-Cache-%23DC382D?logo=redis&logoColor=white)  
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Messaging-%230D3646?logo=apachekafka&logoColor=white)  
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-Search%20%26%20Analytics-%23005571?logo=elasticsearch&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-Containerization-%232496ED?logo=docker&logoColor=white)  
  ![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Orchestration-%232496ED?logo=docker&logoColor=white)  
![Spring Security](https://img.shields.io/badge/Spring%20Security-Authentication-%236DB33F?logo=springsecurity&logoColor=white)  
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-%23D62B00?logo=java&logoColor=white)  
![Kibana](https://img.shields.io/badge/Kibana-Metrics%20%26%20Monitoring-%23005571?logo=kibana&logoColor=white)  


#### Arquitetura

A  Arquitetura segue o padrão de Clean Architecture e Arquitetura hexagonal (ports and adapters) com as seguintes camadas:


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

### Passos para Execução

1. Clone o repositório:
```
git clone [repository-url]
cd Micromerce-Webflux-Reactive/cart
```

2. Execute com Docker Compose:
```bash
docker build -t cart-microservice:latest .
```

```bash
docker-compose up -d
```

3. Verifique os serviços:
```bash
docker-compose ps
```

## Endpoints da API

### 1. Criar Carrinho Básico
```http
POST http://localhost:8082/api/v1/carts
X-User-Id: [user-id]
```

### 2. Criar Carrinho com Itens
```http
POST http://localhost:8082/api/v1/carts/user-cart
Content-Type: application/json

{
    "id": "649a1d8f-945e-4b83-9312-7b5d49c5178a",
    "userId": "18",
    "items": [
        {
            "id": "9b5dcf02-df8a-49f2-9d93-1d1929d71a9e",
            "productId": "98765",
            "quantity": 1,
            "unitPrice": 1000.00
        }
    ],
    "promoCode": "WELCOME18",
    "status": "ACTIVE",
    "createdAt": "2024-11-24T14:15:30",
    "updatedAt": "2024-11-24T14:15:30"
}
```

### 3. Buscar Carrinho
```http
GET http://localhost:8082/api/v1/carts/{cartId}
```

### 4. Adicionar Item ao Carrinho
```http
POST http://localhost:8082/api/v1/carts/{cartId}/items
Content-Type: application/json

{
    "productId": "12345",
    "quantity": 1,
    "unitPrice": 99.99
}
```

### 5. Aplicar Código Promocional
```http
POST http://localhost:8082/api/v1/carts/{cartId}/promo
Content-Type: application/json

{
    "promoCode": "WELCOME18"
}
```

#### Fluxo da Aplicação

1. **Criação do Carrinho**:
   - Usuário cria carrinho básico via endpoint `/carts`
   - Sistema gera ID único e persiste no MongoDB
   - Evento de criação é publicado no Kafka

2. **Adição de Itens**:
   - Itens são adicionados via endpoint `/carts/user-cart`
   - Sistema calcula subtotais e total
   - Cache Redis é atualizado

3. **Promoções**:
   - Código "WELCOME18" oferece R$18,00 de desconto
   - Aplicável em compras acima de R$100,00

4. **Persistência**:
   - MongoDB: Armazenamento principal
   - Redis: Cache de carrinhos ativos
   - Elasticsearch: Indexação para busca

## Monitoramento

- **Métricas**: Disponíveis via Actuator em `/actuator/metrics`
- **Health Check**: Status em `/actuator/health`
- **Prometheus**: Métricas em `/actuator/prometheus`

#### Conclusão

O Micromerce Cart Service representa uma implementação state-of-the-art de um microserviço de carrinho de compras, demonstrando como tecnologias modernas e práticas avançadas de arquitetura podem ser combinadas para criar um sistema robusto e escalável.

A escolha do Spring WebFlux como base da aplicação permite o processamento assíncrono e não-bloqueante, resultando em melhor utilização de recursos e maior capacidade de lidar com cargas concorrentes. A integração com MongoDB, Redis e Kafka cria um ecossistema distribuído resiliente, enquanto o Elasticsearch fornece capacidades avançadas de busca e análise.

O projeto segue rigorosamente os princípios SOLID e Clean Architecture, resultando em um código altamente manutenível e testável. O uso de padrões de projeto como Repository, Factory e Builder simplifica a complexidade do código e melhora sua organização.

A containerização com Docker e a orquestração com Docker Compose garantem que o serviço possa ser facilmente implantado em qualquer ambiente, enquanto as métricas do Prometheus e os endpoints do Actuator fornecem observabilidade completa do sistema em produção.

Este microserviço serve como um exemplo prático de como construir sistemas modernos e escaláveis utilizando tecnologias reativas, demonstrando boas práticas de desenvolvimento e arquitetura que podem ser aplicadas em projetos empresariais de larga escala.



### Desenvolvido por:

Emerson Amorim [@emerson-amorim-dev](https://www.linkedin.com/in/emerson-amorim-dev/)