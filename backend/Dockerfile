# syntax=docker/dockerfile:1
FROM dhi.io/eclipse-temurin:21-jdk-alpine-dev@sha256:301822cf8c408f181c8a7ebb246b52fddeb25f49a20f65153d234df711eb64fb AS builder
WORKDIR /build

# copia solo i file maven che cambiano raramente per sfruttare la cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests dependency:go-offline
COPY . .

ARG revision=0.0.1-SNAPSHOT
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests -Drevision=${revision} clean package

COPY HealthCheck.java .
RUN javac HealthCheck.java

FROM dhi.io/eclipse-temurin:21-alpine@sha256:77c267a66d1ab6e67f09b9342c90568c9f1fae7c32997744f55adefb5707b7d1 AS runtime
WORKDIR /app
COPY --from=builder /build/target/backend*.jar ./backend.jar
COPY --from=builder /build/HealthCheck.class ./HealthCheck.class
EXPOSE 8080
ENV DB_URL=jdbc:mysql://localhost:3306/greentrails
ENV DB_USER=myuser
ENV DB_PASS=mypassword
ENTRYPOINT ["java", "-jar", "backend.jar"]
