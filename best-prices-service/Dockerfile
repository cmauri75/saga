FROM openjdk:17.0.1-jdk-slim

WORKDIR /usr/app

ADD target/*jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
