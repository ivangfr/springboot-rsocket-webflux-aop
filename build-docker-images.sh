#!/usr/bin/env bash

DOCKER_IMAGE_PREFIX="ivanfranchin"
APP_VERSION="1.0.0"

MOVIE_SERVER_APP_NAME="movie-server"
MOVIE_CLIENT_UI_APP_NAME="movie-client-ui"
MOVIE_CLIENT_SHELL_APP_NAME="movie-client-shell"

MOVIE_SERVER_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${MOVIE_SERVER_APP_NAME}:${APP_VERSION}"
MOVIE_CLIENT_UI_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${MOVIE_CLIENT_UI_APP_NAME}:${APP_VERSION}"
MOVIE_CLIENT_SHELL_DOCKER_IMAGE_NAME="${DOCKER_IMAGE_PREFIX}/${MOVIE_CLIENT_SHELL_APP_NAME}:${APP_VERSION}"

SKIP_TESTS="true"

./mvnw clean spring-boot:build-image --projects "$MOVIE_SERVER_APP_NAME" -DskipTests="$SKIP_TESTS" -Dspring-boot.build-image.imageName="$MOVIE_SERVER_DOCKER_IMAGE_NAME"
./mvnw clean spring-boot:build-image --projects "$MOVIE_CLIENT_UI_APP_NAME" -DskipTests="$SKIP_TESTS" -Dspring-boot.build-image.imageName="$MOVIE_CLIENT_UI_DOCKER_IMAGE_NAME"
./mvnw clean spring-boot:build-image --projects "$MOVIE_CLIENT_SHELL_APP_NAME" -DskipTests="$SKIP_TESTS" -Dspring-boot.build-image.imageName="$MOVIE_CLIENT_SHELL_DOCKER_IMAGE_NAME"