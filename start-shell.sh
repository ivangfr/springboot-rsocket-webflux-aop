#!/usr/bin/env bash

echo
echo "Starting movie-client-shell..."

docker run -it --rm --name movie-client-shell \
  -e SPRING_PROFILES_ACTIVE=${1:-default} -e MOVIE_SERVER_HOST=movie-server \
  --network springboot-rsocket-webflux-aop_default \
  docker.mycompany.com/movie-client-shell:1.0.0
