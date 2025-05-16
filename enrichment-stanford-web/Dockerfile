# Builds a docker image from a locally built Maven jar. Requires 'mvn package' to have been run beforehand
# when needed the image can be switched from jre to jdk, but the jdk has about 200MB
FROM eclipse-temurin:11-jre-alpine
#FROM eclipse-temurin:17-jre-alpine

COPY ./target/ROOT-executable.jar /opt/app/ROOT-executable.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/ROOT-executable.jar"]