#!/usr/bin/env bash

source scripts/my-functions.sh

echo
echo "Starting movie-server..."

docker run -d --rm --name movie-server \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=${1:-default} -e MONGODB_HOST=mongodb \
  --network springboot-rsocket-webflux-aop_default \
  --health-cmd="curl -f http://localhost:8080/actuator/health || exit 1" --health-start-period=10s \
  docker.mycompany.com/movie-server:1.0.0

wait_for_container_log "movie-server" "Started"

echo
echo "Starting movie-client-ui..."

docker run -d --rm --name movie-client-ui \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=${1:-default} -e MOVIE_SERVER_HOST=movie-server \
  --network springboot-rsocket-webflux-aop_default \
  --health-cmd="curl -f http://localhost:8081/actuator/health || exit 1" --health-start-period=10s \
  docker.mycompany.com/movie-client-ui:1.0.0

wait_for_container_log "movie-client-ui" "Started"
