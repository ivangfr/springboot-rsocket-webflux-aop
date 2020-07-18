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
  
## Application's Port

| Application  | Netty | Netty RSocket |
| ------------ | ----: | ------------: |
| movie-server | 8081  | 9081          |
| movie-client | 8082  | 9082          |

## Test

- In a terminal, make the following `curl` request
  ```
  curl -i localhost:8082/echo/ola
  ```
  
  It should return
  ```
  HTTP/1.1 200 OK
  OLA
  ```