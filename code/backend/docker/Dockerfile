FROM maven:3.9.0-eclipse-temurin-17 as build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
COPY --from=build /workspace/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
