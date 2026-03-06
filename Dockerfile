FROM eclipse-temurin:21-jdk-jammy AS build
COPY . .
RUN sh mvnw clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/Arisuki-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]