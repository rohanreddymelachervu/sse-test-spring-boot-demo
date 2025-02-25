FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/SSE-Test-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]