# Usa una imagen oficial de Java con Maven
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Segunda fase: correr el .jar
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
