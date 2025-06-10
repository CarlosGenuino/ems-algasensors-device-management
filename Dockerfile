FROM gradle:8.5-jdk21-alpine AS build

WORKDIR /app

# Copiar apenas arquivos de configuração primeiro (para cache)
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# Download dependências
RUN gradle dependencies --no-daemon

# Copiar código fonte
COPY src ./src

# Build
RUN gradle bootJar --no-daemon --parallel

# Imagem final
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root
RUN addgroup --system spring && adduser --system spring --ingroup spring

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]