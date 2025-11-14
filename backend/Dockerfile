# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy pom
COPY pom.xml .

# Download dependencies
RUN ./mvnw -B dependency:go-offline

# Copy source
COPY src src

# Build JAR
RUN ./mvnw -B clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]
