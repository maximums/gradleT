FROM openjdk:11
EXPOSE 8090:8090
RUN mkdir /app
COPY ./build/libs/*.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]