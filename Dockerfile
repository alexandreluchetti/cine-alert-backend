# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -q package -DskipTests 2>/dev/null || \
    (apk add --no-cache maven && mvn -q package -DskipTests)

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S cinealert && adduser -S cinealert -G cinealert
RUN mkdir -p /app/data && chown -R cinealert:cinealert /app

COPY --from=build /app/target/*.jar app.jar
RUN chown cinealert:cinealert app.jar

USER cinealert

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
