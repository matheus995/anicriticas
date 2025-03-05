FROM openjdk:21-slim
WORKDIR /app
COPY target/anicriticas*.jar /app/lol-analyzer*.jar
CMD ["sh", "-c", "java -XX:+UseSerialGC -XX:MaxRAM=192m -Xmx192m -jar anicriticas*.jar"]