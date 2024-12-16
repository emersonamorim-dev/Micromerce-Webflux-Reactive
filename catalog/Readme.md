### Micromerce Catalog Service - Sistema de Carrinho Reativo 🚀 🔄 🌐

O Micromerce Catalog Service é uma implementação moderna e inovadora de um microserviço de Catalog de Produtos, desenvolvido com as mais recentes tecnologias reativas e práticas de arquitetura de software. Este projeto demonstra a construção de um sistema altamente escalável e resiliente, utilizando Spring WebFlux para programação reativa, MongoDB para persistência, Redis para cache distribuído, Kafka para mensageria assíncrona e Elasticsearch para busca avançada.

A arquitetura do serviço foi cuidadosamente projetada seguindo os princípios da arquitetura hexagonal (ports and adapters), garantindo alta coesão e baixo acoplamento. O sistema é totalmente containerizado com Docker, facilitando a implantação e escalabilidade, além de implementar circuit breakers com Resilience4j para maior resiliência em ambientes distribuídos.


#### Tecnologias Utilizadas

- **Java 21**: Última versão LTS do Java com recursos modernos
- **Spring Boot 3.2.0**: Framework base com suporte a programação reativa
- **Spring WebFlux**: Stack reativa para alta performance e escalabilidade
- **Postgres**: Banco de dados principal
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
![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
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
- 


#### Comando para gerar o Jar

```
mvn clean package -DskipTests
```

#### Comando para buildar a imagem

```
docker build -t catalog-microservice:latest .
```

#### Comando para executar o container

```
docker-compose up --build
```




### Endpoints da API
1. Cadastrar um produto:

```
- POST http://localhost:8081/products
Content-Type: application/json
Authorization: Basic Auth:  admin admin

- Requisição para criar um produto:

{
    "name": "Smartphone Samsung",
    "price": 1399.99,
    "description": "Smartphone de última geração com 128GB"
}
```

- Resposta:
```
{
    "id": 10,
    "name": "Smartphone Samsung",
    "price": 1399.99,
    "description": "Smartphone de última geração com 128GB"
}
```




#### Conclusão

O Micromerce Catalog Service representa uma implementação state-of-the-art de um microserviço de Lista de Produtos, demonstrando como tecnologias modernas e práticas avançadas de arquitetura podem ser combinadas para criar um sistema robusto e escalável.

A escolha do Spring WebFlux como base da aplicação permite o processamento assíncrono e não-bloqueante, resultando em melhor utilização de recursos e maior capacidade de lidar com cargas concorrentes. A integração com MongoDB, Redis e Kafka cria um ecossistema distribuído resiliente, enquanto o Elasticsearch fornece capacidades avançadas de busca e análise.

O projeto segue rigorosamente os princípios SOLID e Clean Architecture, resultando em um código altamente manutenível e testável. O uso de padrões de projeto como Repository, Factory e Builder simplifica a complexidade do código e melhora sua organização.

A containerização com Docker e a orquestração com Docker Compose garantem que o serviço possa ser facilmente implantado em qualquer ambiente, enquanto as métricas do Prometheus e os endpoints do Actuator fornecem observabilidade completa do sistema em produção.

Este microserviço serve como um exemplo prático de como construir sistemas modernos e escaláveis utilizando tecnologias reativas, demonstrando boas práticas de desenvolvimento e arquitetura que podem ser aplicadas em projetos empresariais de larga escala.



### Desenvolvido por:

Emerson Amorim [@emerson-amorim-dev](https://www.linkedin.com/in/emerson-amorim-dev/)
