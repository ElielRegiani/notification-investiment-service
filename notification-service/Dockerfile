# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src

# Linux: executable bit + strip CRLF if the wrapper was checked out with Windows line endings
RUN chmod +x gradlew \
	&& sed -i 's/\r$//' gradlew \
	&& ./gradlew bootJar --no-daemon -x test

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/build/libs/app.jar app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
