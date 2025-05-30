FROM docker.io/eclipse-temurin:23-noble
LABEL maintainer="https://github.com/Ghost-chu/Sapling"
USER 0
ENV TZ=UTC
WORKDIR /app
COPY tools/arthas-boot.jar /app/arthas-boot.jar
COPY target/sapling-0.0.1-SNAPSHOT.jar /app/sapling.jar

ENTRYPOINT ["java","-XX:+UseG1GC", "-XX:MaxRAMPercentage=86.0", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar","sapling.jar"]
