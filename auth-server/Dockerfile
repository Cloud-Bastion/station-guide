FROM openjdk:21
WORKDIR /app
COPY build/libs/auth-server.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]