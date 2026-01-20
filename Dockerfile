# build stage
FROM amazoncorretto:17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# run stage
FROM amazoncorretto:17
VOLUME /tmp
ARG JAR_FILE=target/clinical-history-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","/app.jar"]