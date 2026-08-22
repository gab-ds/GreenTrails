# syntax=docker/dockerfile:1
FROM dhi.io/eclipse-temurin:21-jdk-alpine-dev@sha256:04aa633fe442eeab4b17250e0704fc99ed23a21aa91ff0843cba3e8e2b08bdb0 AS builder
WORKDIR /build

# copia solo i file maven che cambiano raramente per sfruttare la cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests dependency:go-offline
COPY . .

ARG revision=0.0.1-SNAPSHOT
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests -Drevision=${revision} clean package

FROM dhi.io/eclipse-temurin:21-alpine@sha256:79a280a147e9e54990ed0e791cf37272a5c0b49003007dc9de5152ae60f6f3be AS runtime
WORKDIR /app
COPY --from=builder /build/target/backend*.jar ./backend.jar
EXPOSE 8080
ENV DB_URL=jdbc:mysql://localhost:3306/greentrails
ENV DB_USER=myuser
ENV DB_PASS=mypassword
ENTRYPOINT ["java", "-jar", "backend.jar"]
