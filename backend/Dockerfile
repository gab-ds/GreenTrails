# syntax=docker/dockerfile:1
FROM dhi.io/eclipse-temurin:21-jdk-alpine-dev@sha256:cdbeee593bf911c1707348ab5dbcb42aa79603e8d2bc96a201b842bc3060bfe5 AS builder
WORKDIR /build

# copia solo i file maven che cambiano raramente per sfruttare la cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests dependency:go-offline
COPY . .

ARG revision=0.0.1-SNAPSHOT
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests -Drevision=${revision} clean package

FROM dhi.io/eclipse-temurin:21-alpine@sha256:06cb316ed64a30e30fac82ce1504cbbce18343ad2956c9c72cc67b862a8744e3 AS runtime
WORKDIR /app
COPY --from=builder /build/target/backend*.jar ./backend.jar
EXPOSE 8080
ENV DB_URL=jdbc:mysql://localhost:3306/greentrails
ENV DB_USER=myuser
ENV DB_PASS=mypassword
ENTRYPOINT ["java", "-jar", "backend.jar"]
