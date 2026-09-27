# Multi-stage Dockerfile for IPL D11 Analytics Engine (Java 21/25 ready)
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy maven wrapper & build descriptor
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Resolve dependencies offline
RUN ./mvnw dependency:go-offline -B || true

# Copy source and build jar
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root user
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

COPY --from=builder /app/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser
EXPOSE 8081

ENTRYPOINT ["java", "-XX:+EnableDynamicAgentLoading", "-jar", "app.jar"]
