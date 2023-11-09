#FROM adoptopenjdk/openjdk11:alpine-jre
FROM openjdk:11-jdk
WORKDIR /opt/app
ARG JAR_FILE=target/xoso-dna-api-1.0.jar
ARG ENV_DEPLOY="--spring.profiles.active="
RUN echo ${ENV_DEPLOY}
COPY ${JAR_FILE} api.jar
EXPOSE 8081
ENV APP_ENV_CONFIG=${ENV_DEPLOY}
CMD java -jar api.jar ${APP_ENV_CONFIG}
