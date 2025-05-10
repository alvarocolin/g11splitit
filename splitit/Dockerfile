# Etapa 1: Compilar el proyecto con Maven
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Crear carpeta de trabajo en la imagen
WORKDIR /app

# Copiar solo el contenido de la carpeta "splitit" (donde está pom.xml y src)
COPY splitit/ /app/

# Ejecutar compilación del proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar la app con JRE optimizado
FROM eclipse-temurin:17

WORKDIR /app

# Copiar el .jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

