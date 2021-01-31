FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/demodock-springboot.jar
COPY ${JAR_FILE} docker-springboot.jar
WORKDIR docker-springboot
EXPOSE 8083
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]
