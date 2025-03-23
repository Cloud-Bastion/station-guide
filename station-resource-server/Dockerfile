FROM openjdk:21
WORKDIR /app
COPY build/libs/station-resource-server.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]