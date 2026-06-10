# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia parent POM e instálalo en el repo local de Maven
COPY pom.xml ./pom.xml
RUN mvn install -N -B -q

# Copia el pom del servicio y precarga dependencias
COPY user-service/pom.xml ./user-service/pom.xml
RUN mvn dependency:go-offline -B -f user-service/pom.xml

# Copia el código fuente y compila
COPY user-service/src ./user-service/src
RUN mvn clean package -DskipTests -B -f user-service/pom.xml

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
