# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven config and source
COPY pom.xml .
COPY src ./src

# Download dependencies
RUN mvn -B dependency:go-offline

# Build JAR
RUN mvn -B clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]
