FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app
ARG JAR_FILE=target/xoso-dna-admin-1.0.jar
ARG ENV_DEPLOY="--spring.profiles.active="
RUN echo ${ENV_DEPLOY}
COPY ${JAR_FILE} admin.jar
EXPOSE 8080
ENV APP_ENV_CONFIG=${ENV_DEPLOY}
CMD java -jar admin.jar ${APP_ENV_CONFIG}