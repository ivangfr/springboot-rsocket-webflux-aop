#!/usr/bin/env bash

source scripts/my-functions.sh

echo
echo "Starting movie-server..."

docker run -d --rm --name movie-server \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=${1:-default} \
  -e MONGODB_HOST=mongodb \
  --network springboot-rsocket-webflux-aop_default \
  --health-cmd='[ -z "$(echo "" > /dev/tcp/localhost/8080)" ] || exit 1' \
  ivanfranchin/movie-server:1.0.0

wait_for_container_log "movie-server" "Started"

echo
echo "Starting movie-client-ui..."

docker run -d --rm --name movie-client-ui \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=${1:-default} \
  -e MOVIE_SERVER_HOST=movie-server \
  --network springboot-rsocket-webflux-aop_default \
  --health-cmd='[ -z "$(echo "" > /dev/tcp/localhost/8081)" ] || exit 1' \
  ivanfranchin/movie-client-ui:1.0.0

wait_for_container_log "movie-client-ui" "Started"
