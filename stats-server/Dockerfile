FROM amazoncorretto:11-alpine-jdk
EXPOSE 9090
COPY target/ewm-stats-server*.jar ewm_stats_server.jar
ENTRYPOINT ["java","-jar","/ewm_stats_server.jar"]
