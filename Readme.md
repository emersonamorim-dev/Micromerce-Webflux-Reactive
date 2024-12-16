#### Micromerce Webflux Reactive - Java com Springboot e Webflux 💳 🚀 🌐

O Micromerce Webflux Reactive é uma aplicação de microserviços desenvolvida para atender às crescentes demandas do comércio eletrônico moderno. Com o objetivo de proporcionar uma experiência de compra fluida e reativa, esta aplicação é composta por três microserviços principais: Cart, Catalog e Payment. Cada um desses serviços é projetado para operar de forma independente, permitindo escalabilidade e flexibilidade na gestão de recursos.

Utilizando tecnologias de ponta como Java 21, Spring Boot e Spring WebFlux, a aplicação é capaz de lidar com um grande volume de requisições simultâneas, garantindo desempenho e responsividade. O microserviço Cart gerencia as funcionalidades do carrinho de compras, utilizando MongoDB para armazenar dados de forma flexível. O Catalog é responsável pela gestão do catálogo de produtos, aproveitando as capacidades do PostgreSQL para garantir integridade e eficiência nas operações de banco de dados. Por fim, o Payment processa transações financeiras, utilizando MySQL para assegurar a confiabilidade e segurança dos dados.

Este projeto não apenas demonstra a aplicação de princípios de arquitetura de microserviços, mas também ilustra a importância da programação reativa na construção de sistemas modernos que atendem às expectativas dos usuários em termos de velocidade e eficiência.

#### Estrutura do Projeto

- **cart**: Microserviço responsável pela gestão de carrinhos de compras.
- **catalog**: Microserviço que gerencia o catálogo de produtos.
- **payment**: Microserviço que processa pagamentos.

#### Tecnologias Utilizadas

- **Java 21**: Linguagem de programação utilizada para o desenvolvimento.
- **Spring Boot**: Framework para construção de aplicações Java.
- **Spring WebFlux**: Módulo do Spring para programação reativa.
- **PostgreSQL**: Banco de dados relacional utilizado pelo microserviço `catalog`.
- **MongoDB**: Banco de dados NoSQL utilizado pelo microserviço `cart`.
- **MySQL**: Banco de dados relacional utilizado pelo microserviço `payment`.
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

#### Para começar a usar o projeto, siga as etapas abaixo:

Clone este repositório em sua máquina local:
```
git clone https://github.com/emersonamorim-dev/Micromerce-Webflux-Reactive.git
```
Navegue até o diretório do projeto

```
cd Micromerce-Webflux-Reactive
```


#### Arquitetura dos Microserviços
A arquitetura do Micromerce Webflux Reactive é baseada no padrão de microserviços, onde cada serviço é responsável por uma funcionalidade específica e se comunica com os demais através de APIs REST. Essa abordagem promove a escalabilidade, a manutenção e a flexibilidade do sistema como um todo.

1. Microserviço Cart
O microserviço Cart é responsável pela gestão do carrinho de compras. Ele permite que os usuários adicionem, removam e visualizem itens no carrinho. Este microserviço utiliza MongoDB como banco de dados NoSQL, que oferece flexibilidade no armazenamento de dados.

#### Padrões e Princípios:

- Repository Pattern: Utilizado para abstrair a lógica de acesso a dados, permitindo que a lógica de negócios não dependa diretamente do banco de dados.
Service Layer Pattern: Separa a lógica de negócios da lógica de apresentação, facilitando a manutenção e a testabilidade.

#### SOLID Principles:

- Single Responsibility Principle (SRP): Cada classe tem uma única responsabilidade, como gerenciar itens do carrinho ou interagir com o banco de dados.
- Dependency Inversion Principle (DIP): As classes de alto nível não dependem de classes de baixo nível, mas de abstrações, facilitando a injeção de dependências.

2. Microserviço Catalog
O microserviço Catalog gerencia o catálogo de produtos disponíveis para os usuários. Ele utiliza PostgreSQL como banco de dados relacional, garantindo integridade referencial e suporte a transações.

#### Padrões e Princípios:

- Data Transfer Object (DTO): Utilizado para transferir dados entre a camada de apresentação e a camada de serviço, evitando a exposição direta das entidades do banco de dados.
- Facade Pattern: Fornece uma interface simplificada para interagir com o sistema de catálogo, ocultando a complexidade subjacente.

#### SOLID Principles:

- Open/Closed Principle (OCP): O sistema é projetado para ser extensível sem modificar o código existente, permitindo a adição de novos tipos de produtos.
- Interface Segregation Principle (ISP): Interfaces são divididas em partes menores e específicas, evitando a obrigatoriedade de implementar métodos não utilizados.


3. Microserviço Payment
O microserviço Payment é responsável por processar pagamentos e gerenciar transações financeiras. Ele utiliza MySQL como banco de dados relacional, garantindo segurança e confiabilidade nas operações financeiras.

Padrões e Princípios:

- Command Query Responsibility Segregation (CQRS): Separa as operações de leitura e escrita, permitindo otimizações específicas para cada tipo de operação.
- Event Sourcing: Armazena o estado do sistema como uma sequência de eventos, permitindo a reconstrução do estado atual a partir do histórico.

#### SOLID Principles:

- Liskov Substitution Principle (LSP): As subclasses devem ser substituíveis por suas classes base, garantindo que o sistema funcione corretamente independentemente da implementação específica.
- Dependency Injection: Facilita a gestão de dependências e a testabilidade, permitindo a troca de implementações de forma simples.


#### Tratamento de Erros
Nossa aplicação utiliza uma abordagem estruturada para o tratamento de erros, garantindo que todas as exceções sejam devidamente capturadas, processadas e respondidas de maneira consistente. Abaixo está a lista de exceções personalizadas e o handler global que utilizamos para gerenciar esses erros.

#### Exceções Personalizadas

1. ErrorCode: Classe base para todos os códigos de erro personalizados.
2. ErrorResponse: Classe que define a estrutura da resposta de erro.
3. PaymentBusinessException: Exceção lançada para erros relacionados à lógica de negócios dos pagamentos.
4. PaymentCancellationException: Exceção lançada quando ocorre um erro durante o cancelamento de um pagamento.
5. PaymentConversionException: Exceção lançada quando ocorre um erro durante a conversão de dados de pagamento.
6. PaymentException: Exceção genérica para erros relacionados a pagamentos.
7. PaymentIntegrationException: Exceção lançada quando ocorre um erro na integração com sistemas externos de pagamento.
8. PaymentMapperException: Exceção lançada quando ocorre um erro durante o mapeamento de dados de pagamento.
9. PaymentNotFoundException: Exceção lançada quando um pagamento não é encontrado.
10. PaymentPresentationException: Exceção lançada para erros relacionados à camada de apresentação dos pagamentos.
11. PaymentProcessingException: Exceção lançada quando ocorre um erro durante o processamento de um pagamento.
12. PaymentValidationException: Exceção lançada quando os dados de pagamento não passam pela validação.
13. PaymentValueConversionException: Exceção lançada quando ocorre um erro durante a conversão de valores de pagamento.
14. RefundProcessingException: Exceção lançada quando ocorre um erro durante o processamento de um reembolso.

#### Handler Global de Exceções
GlobalExceptionHandler: Classe responsável por capturar todas as exceções lançadas pela aplicação e retornar uma resposta de erro consistente. Este handler centraliza o tratamento de erros, facilitando a manutenção e garantindo que todas as exceções sejam tratadas de maneira uniforme.
Essa abordagem estruturada para o tratamento de erros garante que nossa aplicação seja robusta e que os usuários recebam feedback claro e consistente em caso de erros. Além disso, facilita a manutenção e a evolução do código, permitindo que novas exceções sejam adicionadas e tratadas de maneira simples e eficiente.


#### Documentação da API com Swagger
A documentação da API para nosso serviço de processamento de pagamentos é gerada utilizando o Swagger, uma ferramenta poderosa para descrever e interagir com APIs RESTful. O Swagger fornece uma interface amigável e interativa que permite aos desenvolvedores explorar e testar os endpoints da API de maneira eficiente.

#### Acessando a Documentação
Para acessar a documentação da API, navegue até a URL do Swagger fornecida. A interface do Swagger oferece uma visão geral completa de todos os endpoints disponíveis, incluindo:

- Descrições dos Endpoints: Cada endpoint é descrito com detalhes sobre sua funcionalidade.

- Métodos HTTP: Informações sobre os métodos HTTP suportados (GET, POST, etc.).
- Parâmetros: Lista de parâmetros necessários para cada endpoint.
- Exemplos de Requisições e Respostas: Exemplos de como estruturar as requisições e como serão as respostas esperadas.

#### Endpoints Disponíveis

Home
-GET /: Endpoint raiz que fornece informações gerais sobre a API.
-GET /info: Obtém informações detalhadas sobre a API e endpoints disponíveis.
-GET /api: Acesso às configurações para ativar o Windows.

Payment
- GET /api/v1/payments: Lista todos os pagamentos.
- GET /api/v1/payments/{id}: Obtém um pagamento específico pelo ID.
- GET /api/v1/payments/order/{orderId}: Obtém pagamentos por ID da encomenda.
- GET /api/v1/payments/customer/{customerId}: Obtém pagamentos por ID do cliente.
- POST /api/v1/payments/{id}/refund: Realiza o reembolso de um pagamento.
- POST /api/v1/payments/{id}/cancel: Cancela um pagamento.
- POST /api/v1/payments: Processa um novo pagamento.

#### Autenticação
Para acessar endpoints que requerem autenticação, utilize o botão "Authorize" na interface do Swagger e insira suas credenciais conforme necessário.

A documentação do Swagger é uma ferramenta essencial para entender e interagir com nossa API de processamento de pagamentos. Ela fornece uma maneira intuitiva de explorar os endpoints, testar requisições e entender as respostas esperadas.





#### Conclusão
O Micromerce Webflux Reactive representa uma solução robusta e escalável para o comércio eletrônico, integrando microserviços que operam de forma coesa para oferecer uma experiência de compra excepcional. Através da utilização de tecnologias modernas e práticas de desenvolvimento ágeis, este projeto não apenas atende às necessidades atuais do mercado, mas também se posiciona para evoluir com as futuras demandas dos consumidores.

A modularidade dos microserviços permite que cada componente seja desenvolvido, testado e implantado de forma independente, facilitando a manutenção e a adição de novas funcionalidades. Com a implementação de práticas de segurança e gerenciamento de dados eficazes, a aplicação assegura a proteção das informações dos usuários e a integridade das transações.

Em resumo, o Micromerce Webflux Reactive é mais do que uma simples aplicação; é uma demonstração do potencial dos microserviços e da programação reativa na construção de soluções inovadoras e eficientes para o comércio eletrônico. O projeto está aberto a contribuições e melhorias, convidando desenvolvedores a se juntarem a esta jornada de transformação digital.

### Desenvolvido por:
Emerson Amorim [@emerson-amorim-dev](https://www.linkedin.com/in/emerson-amorim-dev/)
