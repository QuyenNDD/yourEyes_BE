FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests && ls -lh target/

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/myApp-0.0.1-SNAPSHOT.jar ./myApp.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/myApp.jar"]
