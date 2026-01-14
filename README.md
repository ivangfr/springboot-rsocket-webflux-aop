# springboot-rsocket-webflux-aop

The goal of this project is to play with [`RSocket`](https://rsocket.io/) protocol. To do this, we will implement three [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) Java applications: `movie-server`, `movie-client-shell` and `movie-client-ui`. For storage, the reactive NoSQL database [`MongoDB`](https://www.mongodb.com/) is used. All streaming of movie events and logging is handled by AOP (Aspect Oriented Programming).

## Proof-of-Concepts & Articles

On [ivangfr.github.io](https://ivangfr.github.io), I have compiled my Proof-of-Concepts (PoCs) and articles. You can easily search for the technology you are interested in by using the filter. Who knows, perhaps I have already implemented a PoC or written an article about what you are looking for.

## Additional Readings

- \[**Medium**\] [**RSocket Crypto Server Tutorial**](https://medium.com/@ivangfr/list/rsocket-crypto-server-tutorial-651560b2159a)
- \[**Medium**\] [**Implementing and Securing a Spring Boot RSocket App using Keycloak for IAM**](https://itnext.io/implementing-and-securing-a-spring-boot-rsocket-app-with-keycloak-5a6c74bf453d)

## Project Diagram

![project-diagram](documentation/project-diagram.jpeg)

## Applications

- ### movie-server

  `Spring Boot` Java Web application that exposes REST API endpoints or RSocket routes to manage `movies`. Movies data are stored in reactive `MongoDB`.
  
  `movie-server` has the following profiles:
  
  - `default`
     - starts REST API on port `8080` and uses `HTTP`.
     
  - `rsocket-tcp`
     - starts REST API on port `8080` and uses `HTTP`.
     - start RSocket on port `7000` and uses `TCP`.
     
  - `rsocket-websocket`
     - starts REST API on port `8080` and uses `HTTP`.
     - starts RSocket with mapping-path `/rsocket` and uses `WebSocket`.

- ### movie-client-shell

  `Spring Boot` Shell Java application that has a couple of commands to interact with `movie-server`.
  
  The picture below shows those commands:
  
  ![movie-client-shell](documentation/movie-client-shell.jpeg)

  It has the following profiles:
  
  - `default`
     - starts shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`.
     
  - `rsocket-tcp`
     - starts shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`.
     - starts shell with enabled commands to call `movie-server` RSocket routes using `TCP`.
     
  - `rsocket-websocket`
     - starts shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`.
     - starts shell with enabled commands to call `movie-server` RSocket routes using `WebSocket`.

- ### movie-client-ui

  `Spring Boot` Java Web application that uses [`Thymeleaf`](https://www.thymeleaf.org/) and `WebSocket` to show at real-time all the events generated when movies are added, deleted, liked and disliked.

  ![movie-client-ui](documentation/movie-client-ui.jpeg)

  `movie-client-ui` has the following profiles:
  
  - `default`
     - starts REST API on port `8081` and uses `HTTP`.
     - does not connect to `movie-server` through `RSocket`; does not receive movie events.
     
  - `rsocket-tcp`
     - starts REST API on port `8080` and uses `HTTP`.
     - connects to `movie-server` through `RSocket` using `TCP`; receives movie events.
     
  - `rsocket-websocket`
     - starts REST API on port `8080` and uses `HTTP`.
     - connects to `movie-server` through `RSocket` using `WebSocket`; receives movie events.

## Demo

The GIF below shows a user running some commands in `movie-client-shell` (terminal on the right). The top-right terminal is running `movie-server`, and the bottom-right terminal runs `movie-client-ui`. In the background, a browser displays the movie events. 

![demo](documentation/demo.gif)

## Prerequisites

- [`Java 25`](https://www.oracle.com/java/technologies/downloads/#java25) or higher.
- A containerization tool (e.g., [`Docker`](https://www.docker.com), [`Podman`](https://podman.io), etc.)

## Start Environment

Open a terminal and inside the `springboot-rsocket-webflux-aop` root folder run:
```bash
docker compose up -d
```

## Running applications with Maven

- **movie-server**

  Open a new terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run one of the following profile's command:
  
  | Profile           | Command                                                                                           |
  |-------------------|---------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | ./mvnw clean spring-boot:run --projects movie-server -Dspring-boot.run.profiles=rsocket-tcp       |
  | rsocket-websocket | ./mvnw clean spring-boot:run --projects movie-server -Dspring-boot.run.profiles=rsocket-websocket |
  | default           | ./mvnw clean spring-boot:run --projects movie-server                                              |
  
- **movie-client-shell**

  Open a new terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run the profile's command you picked to run `movie-server`:
  
  | Profile           | Commands                                                                                                      |
  |-------------------|---------------------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | export SPRING_PROFILES_ACTIVE=rsocket-tcp && ./mvnw clean spring-boot:run --projects movie-client-shell       |
  | rsocket-websocket | export SPRING_PROFILES_ACTIVE=rsocket-websocket && ./mvnw clean spring-boot:run --projects movie-client-shell |
  | default           | export SPRING_PROFILES_ACTIVE=default && ./mvnw clean spring-boot:run --projects movie-client-shell           |

- **movie-client-ui**

  Open a new terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run the profile's command you picked to run `movie-server`:
  
  | Profile           | Command                                                                                              |
  |-------------------|------------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | ./mvnw clean spring-boot:run --projects movie-client-ui -Dspring-boot.run.profiles=rsocket-tcp       |
  | rsocket-websocket | ./mvnw clean spring-boot:run --projects movie-client-ui -Dspring-boot.run.profiles=rsocket-websocket |
  | default           | ./mvnw clean spring-boot:run --projects movie-client-ui                                              |

## Running applications as Docker containers

- ### Build Docker images

  - In a terminal, make sure you are in the `springboot-rsocket-webflux-aop` root folder.
  - Run the following script to build the Docker images:
    ```bash
    ./build-docker-images.sh
    ```

- ### Environment variables

  - **movie-server**

    | Environment Variable | Description                                                       |
    |----------------------|-------------------------------------------------------------------|
    | `MONGODB_HOST`       | Specify host of the `Mongo` database to use (default `localhost`) |
    | `MONGODB_PORT`       | Specify port of the `Mongo` database to use (default `27017`)     |

  - **movie-client-shell**

    | Environment Variable | Description                                                     |
    |----------------------|-----------------------------------------------------------------|
    | `MOVIE_SERVER_HOST`  | Specify host of the `movie-server` to use (default `localhost`) |

  - **movie-client-ui**

    | Environment Variable | Description                                                     |
    |----------------------|-----------------------------------------------------------------|
    | `MOVIE_SERVER_HOST`  | Specify host of the `movie-server` to use (default `localhost`) |

- ### Start Docker containers

  - In a terminal, make sure you are inside the `springboot-rsocket-webflux-aop` root folder.
  - Run following command:
    - rsocket-tcp
      ```bash
      ./start-server-and-ui.sh rsocket-tcp && ./start-shell.sh rsocket-tcp
      ```
    - rsocket-websocket
      ```bash
      ./start-server-and-ui.sh rsocket-websocket && ./start-shell.sh rsocket-websocket
      ```
    - default
      ```bash
      ./start-server-and-ui.sh && ./start-shell.sh
      ```

## Application's URL

| Application     | Type     | Transport | URL                         |
|-----------------|----------|-----------|-----------------------------|
| movie-server    | RSocket  | TCP       | tcp://localhost:7000        |
| movie-server    | RSocket  | WebSocket | ws://localhost:8080/rsocket |
| movie-server    | REST     | HTTP      | http://localhost:8080       |
| movie-client-ui | Website  | HTTP      | http://localhost:8081       |

> **Note**: you can see the clients connected to `movie-server` by calling the `info` actuator endpoint.
> ```bash
> curl -i localhost:8080/actuator/info
> ```

## Playing Around with movie-client-shell commands

> **Note**: to run the commands below, you must start `movie-server` and `movie-client-shell` with `rsocket-tcp` or `rsocket-websocket` profiles.

- Open a browser and access `movie-client-ui` at http://localhost:8081

- Go to `movie-client-shell` terminal.

- Add a movie using RSocket (`Request-Response`):
  ```bash
  add-movie-rsocket --imdb aaa --title "RSocketland"
  ```
  
  It should return:
  ```json
  {"imdb":"aaa","title":"RSocketland","lastModifiedDate":"2020-07-20T12:43:39.857248","likes":0,"dislikes":0}
  ```
  
  A `+` action should be displayed in `movie-client-ui`.
  
- Add a movie using REST:
  ```bash
  add-movie-rest --imdb bbb --title "I, REST"
  ```
  
  It should return:
  ```json
  {"imdb":"bbb","title":"I, REST","lastModifiedDate":"2020-07-20T12:44:13.266657","likes":0,"dislikes":0}
  ```
  
  A `+` action should be displayed in `movie-client-ui`.
  
- Send a like to `RSocketland` movie using RSocket (`Fire-And-Forget`):
  ```bash
  like-movie-rsocket --imdb aaa
  ```
  
  It should return:
  ```text
  Like submitted
  ```
  
  A `thumbs-up` action should be displayed in `movie-client-ui`.

- Get all movies using RSocket (`Request-Stream`):
  ```bash
  get-movies-rsocket
  ```
  
  It should return:
  ```text
  {"imdb":"aaa","title":"RSocketland","lastModifiedDate":"2020-07-20T12:56:34.565","likes":1,"dislikes":0}
  {"imdb":"bbb","title":"I, REST","lastModifiedDate":"2020-07-20T12:56:26.846","likes":0,"dislikes":0}
  ```
  
- Select movies using RSocket (`Channel`).
  ```bash
  select-movies-rsocket --imdbs aaa,bbb
  ```
  
  It should return:
  ```text
  | IMDB: aaa        | TITLE: RSocketland                    | LIKES: 1     | DISLIKES: 0     |
  | IMDB: bbb        | TITLE: I, REST                        | LIKES: 0     | DISLIKES: 0     |
  ```
  
- Delete movie `RSocketland` using RSocket (`Request-Response`) and movie `I, REST` using REST.
  ```bash
  delete-movie-rsocket --imdb aaa
  delete-movie-rest --imdb bbb
  ```
  
  It should return, as response, the IMDB of the movies.
  
  A `-` actions should be displayed in `movie-client-ui`.
  
- **Simulation**

  There are two scripts that contain some commands to add, retrieve, like, dislikes and delete movies. One uses REST, and the other uses RSocket to communicate with `movie-server`. At the end of the script execution, it's shown the `Execution Time` in `milliseconds`.
  
  - If you are running the applications with Maven:
    - REST
      ```bash
      script ../src/main/resources/simulation-rest.txt
      ```
    - RSocket
      ```bash
      script ../src/main/resources/simulation-rsocket.txt
      ```

  - If you are running the applications as Docker containers:
    - REST
      ```bash
      script /workspace/BOOT-INF/classes/simulation-rest.txt
      ```
    - RSocket
      ```bash
      script /workspace/BOOT-INF/classes/simulation-rsocket.txt
      ```

## Useful Commands

- **MongoDB**

  Find all movies:
  ```bash
  docker exec -it mongodb mongosh moviedb
  db.movies.find()
  ```
  > Type `exit` to get out of `MongoDB` shell.

## Shutdown

- To stop `movie-client-shell`, go to the terminal where it is running and type `exit`.
- To stop `movie-server` and `movie-client-ui`:
  - If you start them with Maven, go to the terminals where they are running and press `Ctrl+C`.
  - If you start them as Docker containers, go to a terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run the following command:
    ```bash
    ./stop-server-and-ui.sh
    ```
- To stop and remove docker compose `mongodb` container, network and volumes, go to a terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run the command below:
  ```bash
  docker compose down -v
  ```

## Cleanup

To remove the Docker images created by this project, go to a terminal and, inside the `springboot-rsocket-webflux-aop` root folder, run the script below:
```bash
./remove-docker-images.sh
```

## References

- https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket
- https://spring.io/blog/2020/05/12/getting-started-with-rsocket-servers-calling-clients
- https://grapeup.com/blog/reactive-service-to-service-communication-with-rsocket-introduction/

## Issues

The autocomplete is not working when we run `movie-client-shell` as Docker container.
