FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app
ARG JAR_FILE=target/xoso-dna-schedule-1.0.jar
ARG ENV_DEPLOY="--spring.profiles.active="
RUN echo ${ENV_DEPLOY}
COPY ${JAR_FILE} schedule.jar
ENV APP_ENV_CONFIG=${ENV_DEPLOY}
CMD java -jar schedule.jar ${APP_ENV_CONFIG}