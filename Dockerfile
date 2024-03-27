FROM openjdk:17-alpine

VOLUME /tmp

ADD opa-client/target/odm-platform-up-policy-opa-client-*.jar ./
COPY opa-policy-server/target/odm-platform-up-policy-opa-server-*.jar ./application.jar

ARG SPRING_PROFILES_ACTIVE=docker
ARG JAVA_OPTS
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG FLYWAY_SCHEMA=flyway
ARG FLYWAY_SCRIPTS_DIR=postgresql
ARG OPA_HOSTNAME=localhost
ARG OPA_PORT=8181
ARG SPRING_PORT=9001
ARG SPRING_PROPS

ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}
ENV JAVA_OPTS ${JAVA_OPTS}
ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}
ENV FLYWAY_SCHEMA ${FLYWAY_SCHEMA}
ENV FLYWAY_SCRIPTS_DIR ${FLYWAY_SCRIPTS_DIR}
ENV OPA_HOSTNAME ${OPA_HOSTNAME}
ENV OPA_PORT ${OPA_PORT}
ENV SPRING_PORT ${SPRING_PORT}
ENV SPRING_PROPS ${SPRING_PROPS}

EXPOSE $SPRING_PORT

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE $SPRING_PROPS -jar ./application.jar" ]