FROM nexus3.inspq.qc.ca:5000/inspq/oraclejdk:latest

ARG APP_VERSION=1.0.0-SNAPSHOT
ENV KEYCLOAK_URL=""
ENV KEYCLOAK_REALM="master"
ENV KEYCLOAK_CLIENT_ID=""
ENV KEYCLOAK_CLIENT_SECRET=""
ENV SX5_HABILITATION_SERVICE_URL=""
ENV SX5_HABILITATION_UI_URL=""
ENV KEYCLOAK_ENABLED=false
ENV KEYCLOAK_ADMIN_REALM=master
ENV KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
ENV KEYCLOAK_ADMIN_USERNAME=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
ENV EMR_SERVICE_HOME=/opt/emr-service

EXPOSE 8080
RUN mkdir -p $EMR_SERVICE_HOME
ADD target/emr-service-$APP_VERSION.jar $EMR_SERVICE_HOME/emr-service.jar
ADD docker-entrypoint.sh $EMR_SERVICE_HOME/docker-entrypoint.sh
RUN chmod a+x $EMR_SERVICE_HOME/docker-entrypoint.sh

ENTRYPOINT "$EMR_SERVICE_HOME/docker-entrypoint.sh"