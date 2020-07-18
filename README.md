# springboot-rsocket

## Start server and client

- **movie-server**
  ```
  ./mvnw clean spring-boot:run --projects movie-server
  ```
  
- **movie-client**
  ```
  ./mvnw clean spring-boot:run --projects movie-client
  ```
  
## Application's URL

| Application  | Type    | URL                   |
| ------------ | ------: | --------------------: |
| movie-server | RSocket | tcp://localhost:7000  |
| movie-client | REST    | http://localhost:8080 |

## movie-client Endpoint Call Samples

- **create-movie**
  ```
  curl -i -X POST localhost:8080/movies -H 'Content-Type: application/json' -d '{"imdb": "aaa", "title": "movie aaa"}'
  ```

- **get-movies**
  ```
  curl -i localhost:8080/movies
  ```

- **get-movie**
  ```
  curl -i localhost:8080/movies/abc
  ```

- **delete-movie**
  ```
  curl -i -X DELETE localhost:8080/movies/abc
  ```

## Useful Commands & Links

- **MongoDB**

  Find all movies
  ```
  docker exec -it mongodb mongo
  db.movies.find()
  ```
  > Type `exit` to get out of `MongoDB` shell