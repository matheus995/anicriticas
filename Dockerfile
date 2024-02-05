FROM openjdk:21-slim
WORKDIR /app
COPY target/lol-analyzer*.jar /app/lol-analyzer*.jar
COPY .env /app/.env
CMD ["sh", "-c", "java -XX:+UseSerialGC -XX:MaxRAM=192m -Xmx192m -jar lol-analyzer*.jar"]