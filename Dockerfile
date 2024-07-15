# Builds a docker image from a locally built Maven war. Requires 'mvn package' to have been run beforehand
# when needed the image can be switched from jre to jdk, but the jdk has about 200MB
FROM eclipse-temurin:11-jre-alpine
#FROM eclipse-temurin:17-jre-alpine

COPY ./enrichment-web/target/enrichment-web-executable.jar /opt/app/enrichment-web-executable.jar
COPY k8s/enrich/ /app/enrich/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/enrichment-web-executable.jar"]

#From tomcat:9.0.73-jre11-temurin-focal
#COPY enrichment-web/target/enrichment-web-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/enrichment-web.war
##copy static configurations 
#COPY k8s/enrich/ /app/enrich/
#CMD ["catalina.sh","run"]
