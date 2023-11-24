From tomcat:9.0.73-jre11-temurin-focal
COPY enrichment-web/target/enrichment-web-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/enrichment-web.war
CMD ["catalina.sh","run"]
