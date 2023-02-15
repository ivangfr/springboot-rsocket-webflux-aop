#!/usr/bin/env bash

if [ "$1" = "native" ];
then
  ./mvnw clean -Pnative spring-boot:build-image --projects movie-server -DskipTests
  ./mvnw clean -Pnative spring-boot:build-image --projects movie-client-ui -DskipTests
  ./mvnw clean -Pnative spring-boot:build-image --projects movie-client-shell -DskipTests
else
  ./mvnw clean compile jib:dockerBuild --projects movie-server
  ./mvnw clean compile jib:dockerBuild --projects movie-client-ui
  ./mvnw clean compile jib:dockerBuild --projects movie-client-shell
fi