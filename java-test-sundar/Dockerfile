#Start with a base image containing Java runtime
FROM openjdk:11-slim as build

#Information around who maintains the image
LABEL maintainer="sundar.com"

# Add the application's jar to the container
COPY target/java-test-sundar-0.0.1-SNAPSHOT.jar java-test-sundar-0.0.1-SNAPSHOT.jar

#execute the application
ENTRYPOINT ["java","-jar","/java-test-sundar-0.0.1-SNAPSHOT.jar"]