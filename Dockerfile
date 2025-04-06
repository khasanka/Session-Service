# Use OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# App home
WORKDIR /app

# Copy built fat JAR and config
<<<<<<< HEAD
COPY target/sessionDTO-service.jar sessionDTO-service.jar
=======
COPY target/session-service.jar session-service.jar
>>>>>>> main
COPY config.yml config.yml

# Expose app and admin ports
EXPOSE 8080
EXPOSE 8081

# Run command
<<<<<<< HEAD
CMD ["java", "-jar", "sessionDTO-service.jar", "server", "config.yml"]
=======
CMD ["java", "-jar", "session-service.jar", "server", "config.yml"]
>>>>>>> main
