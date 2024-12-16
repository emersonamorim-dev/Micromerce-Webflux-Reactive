# Etapa 1: Build
FROM maven:3.9.5-amazoncorretto-21 AS builder

WORKDIR /app

# Copie apenas o POM primeiro
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Baixe as dependências
RUN mvn dependency:resolve -B

# Copie o código-fonte
COPY src ./src

# Compile a aplicação
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime
FROM amazoncorretto:21.0.1-alpine

# Instale dependências necessárias
RUN apk add --no-cache tzdata

# Configure o timezone
ENV TZ=America/Sao_Paulo

WORKDIR /app

# Copie apenas o JAR da etapa de build
COPY --from=builder /app/target/catalog-0.0.1-SNAPSHOT.jar app.jar

# Configurações da JVM para container
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Duser.timezone=America/Sao_Paulo"

# Exponha a porta
EXPOSE 8081

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
