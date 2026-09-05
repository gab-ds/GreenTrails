# syntax=docker/dockerfile:1
FROM dhi.io/eclipse-temurin:21-jdk-alpine-dev@sha256:a106a33ad38786bb643711d311f667a21c69d2cbdb0716f27accc680217e21b6 AS builder
WORKDIR /build

# copia solo i file maven che cambiano raramente per sfruttare la cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests dependency:go-offline
COPY . .

ARG revision=0.0.1-SNAPSHOT
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests -Drevision=${revision} clean package

FROM dhi.io/eclipse-temurin:21-alpine@sha256:d00b984738d9cdd1f2b6e8aeb0547228fd6a973e39dc1e3be913dc3cb27af0e9 AS runtime
WORKDIR /app
COPY --from=builder /build/target/backend*.jar ./backend.jar
EXPOSE 8080
ENV DB_URL=jdbc:mysql://localhost:3306/greentrails
ENV DB_USER=myuser
ENV DB_PASS=mypassword
ENTRYPOINT ["java", "-jar", "backend.jar"]
