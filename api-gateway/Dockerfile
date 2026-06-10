# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia parent POM e instálalo en el repo local de Maven
COPY pom.xml ./pom.xml
RUN mvn install -N -B -q

# Copia el pom del servicio y precarga dependencias
COPY api-gateway/pom.xml ./api-gateway/pom.xml
RUN mvn dependency:go-offline -B -f api-gateway/pom.xml

# Copia el código fuente y compila
COPY api-gateway/src ./api-gateway/src
RUN mvn clean package -DskipTests -B -f api-gateway/pom.xml

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
