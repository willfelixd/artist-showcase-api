# Etapa 1 — Build
# Usa uma imagem com JDK 17 + Gradle para compilar o projeto
FROM gradle:8.7-jdk17 AS build

WORKDIR /app

# Copia os arquivos de configuração do Gradle primeiro
# (separado do código para aproveitar cache de camadas do Docker)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Baixa as dependências (essa camada é cacheada enquanto o build.gradle não mudar)
RUN gradle dependencies --no-daemon

# Copia o código fonte e compila
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# Etapa 2 — Runtime
# Imagem menor, só com JRE 17 para rodar o .jar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia só o .jar gerado na etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]