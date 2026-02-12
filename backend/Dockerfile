FROM eclipse-temurin:21-ubi10-minimal AS builder
WORKDIR /build

# copia solo i file maven che cambiano raramente per sfruttare la cache
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw -B -DskipTests dependency:go-offline
COPY . .

ARG revision=0.0.1-SNAPSHOT
RUN ./mvnw -B -DskipTests -Drevision=${revision} clean package

FROM eclipse-temurin:21-ubi10-minimal AS runtime
WORKDIR /app
COPY --from=builder /build/target/backend*.jar ./backend.jar
EXPOSE 8080
ENV DB_URL=jdbc:mysql://localhost:3306/greentrails
ENV DB_USER=myuser
ENV DB_PASS=mypassword
ENTRYPOINT ["java", "-jar", "backend.jar"]