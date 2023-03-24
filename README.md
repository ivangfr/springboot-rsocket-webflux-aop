# springboot-rsocket-webflux-aop

The goal of this project is to play with [`RSocket`](https://rsocket.io/) protocol. For it, we will implement three [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java applications: `movie-server`, `movie-client-shell` and `movie-client-ui`. As storage, it's used the reactive NoSQL database [`MongoDB`](https://www.mongodb.com/). All the streaming of movie events and the logging are handling by AOP (Aspect Oriented Programming).

## Project Diagram

![project-diagram](documentation/project-diagram.jpeg)

## Applications

- ### movie-server

  `Spring Boot` Java Web application that exposes REST API endpoints or RSocket routes to manage `movies`. Movies data are stored in reactive `MongoDB`.
  
  `movie-server` has the following profiles:
  
  - `default`
     - start REST API on port `8080` and uses `HTTP`
     
  - `rsocket-tcp`
     - start REST API on port `8080` and uses `HTTP`
     - start RSocket on port `7000` and uses `TCP`
     
  - `rsocket-websocket`
     - start REST API on port `8080` and uses `HTTP`
     - start RSocket with mapping-path `/rsocket` and uses `WebSocket`

- ### movie-client-shell

  `Spring Boot` Shell Java application that has a couple of commands to interact with `movie-server`.
  
  The picture below show those commands
  
  ![movie-client-shell](documentation/movie-client-shell.jpeg)

  It has the following profiles:
  
  - `default`
     - start shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`
     
  - `rsocket-tcp`
     - start shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`
     - start shell with enabled commands to call `movie-server` RSocket routes using `TCP`
     
  - `rsocket-websocket`
     - start shell with enabled commands to call `movie-server` REST API endpoints using `HTTP`
     - start shell with enabled commands to call `movie-server` RSocket routes using `WebSocket`

- ### movie-client-ui

  `Spring Boot` Java Web application that uses [`Thymeleaf`](https://www.thymeleaf.org/) and `Websocket` to show at real-time all the events generated when movies are added, deleted, liked and disliked.

  ![movie-client-ui](documentation/movie-client-ui.jpeg)

  `movie-client-ui` has the following profiles:
  
  - `default`
     - start REST API on port `8081` and uses `HTTP`
     - does not connect to `movie-server` through `RSocket`; does not receive movie events;
     
  - `rsocket-tcp`
     - start REST API on port `8080` and uses `HTTP`
     - connects to `movie-server` through `RSocket`using `TCP`; receives movie events;
     
  - `rsocket-websocket`
     - start REST API on port `8080` and uses `HTTP`
     - connects to `movie-server` through `RSocket`using `WebSocket`; receives movie events;

## Demo

The GIF below shows a user running some commands in `movie-client-shell`, terminal on the right. In the right-top terminal is running `movie-server` and in the right-bottom, `movie-client-ui`. On the background, there's a browser where movie events are displayed. 

![demo](documentation/demo.gif)

## Prerequisites

- [`Java 17+`](https://www.oracle.com/java/technologies/downloads/#java17)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)

## Start Environment

- Open a terminal and inside `springboot-rsocket-webflux-aop` root folder run
  ```
  docker-compose up -d
  ```

- Wait for `mongodb` Docker container to be up and running. To check it, run
  ```
  docker-compose ps
  ```

## Running applications with Maven

- **movie-server**

  Open a new terminal and, inside `springboot-rsocket-webflux-aop` root folder, run one of the following profile's command
  
  | Profile           | Command                                                                                           |
  |-------------------|---------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | ./mvnw clean spring-boot:run --projects movie-server -Dspring-boot.run.profiles=rsocket-tcp       |
  | rsocket-websocket | ./mvnw clean spring-boot:run --projects movie-server -Dspring-boot.run.profiles=rsocket-websocket |
  | default           | ./mvnw clean spring-boot:run --projects movie-server                                              |
  
- **movie-client-shell**

  Open a new terminal and, inside `springboot-rsocket-webflux-aop` root folder, run the following command to build the executable jar file
  ```
  ./mvnw clean package --projects movie-client-shell -DskipTests
  ```

  To start `movie-client-shell`, run the profile's command you picked to run `movie-server`
  
  | Profile           | Commands                                                                                                    |
  |-------------------|-------------------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | export SPRING_PROFILES_ACTIVE=rsocket-tcp && ./movie-client-shell/target/movie-client-shell-1.0.0.jar       |
  | rsocket-websocket | export SPRING_PROFILES_ACTIVE=rsocket-websocket && ./movie-client-shell/target/movie-client-shell-1.0.0.jar |
  | default           | export SPRING_PROFILES_ACTIVE=default && ./movie-client-shell/target/movie-client-shell-1.0.0.jar           |

- **movie-client-ui**

  Open a new terminal and, inside `springboot-rsocket-webflux-aop` root folder, run the profile's command you picked to run `movie-server`
  
  | Profile           | Command                                                                                              |
  |-------------------|------------------------------------------------------------------------------------------------------|
  | rsocket-tcp       | ./mvnw clean spring-boot:run --projects movie-client-ui -Dspring-boot.run.profiles=rsocket-tcp       |
  | rsocket-websocket | ./mvnw clean spring-boot:run --projects movie-client-ui -Dspring-boot.run.profiles=rsocket-websocket |
  | default           | ./mvnw clean spring-boot:run --projects movie-client-ui                                              |

## Running applications as Docker containers

- ### Build Docker images

  - In a terminal, make sure you are in `springboot-rsocket-webflux-aop` root folder
  - Run the following script to build the Docker images
    - JVM
      ```
      ./docker-build.sh
      ```
    - Native (it's not working yet, see [Issues](#issues))
      ```
      ./docker-build.sh native
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

  - In a terminal, make sure you are inside `springboot-rsocket-webflux-aop` root folder
  - Run following command
    - rsocket-tcp
      ```
      ./start-server-and-ui.sh rsocket-tcp && ./start-shell.sh rsocket-tcp
      ```
    - rsocket-websocket
      ```
      ./start-server-and-ui.sh rsocket-websocket && ./start-shell.sh rsocket-websocket
      ```
    - default
      ```
      ./start-server-and-ui.sh && ./start-shell.sh
      ```

## Application's URL

| Application     | Type     | Transport | URL                         |
|-----------------|----------|-----------|-----------------------------|
| movie-server    | RSocket  | TCP       | tcp://localhost:7000        |
| movie-server    | RSocket  | WebSocket | ws://localhost:8080/rsocket |
| movie-server    | REST     | HTTP      | http://localhost:8080       |
| movie-client-ui | Website  | HTTP      | http://localhost:8081       |

> **Note**: you can see the clients connected to `movie-server` by calling the `info` actuator endpoint
> ```
> curl -i localhost:8080/actuator/info
> ```

## Playing Around with movie-client-shell commands

> **Note**: to run the commands below, you must start `movie-server` and `movie-client-shell` with `rsocket-tcp` or `rsocket-websocket` profiles

- Open a browser and access `movie-client-ui` at http://localhost:8081

- Go to `movie-client-shell` terminal

- Add a movie using RSocket (`Request-Response`) 
  ```
  add-movie-rsocket --imdb aaa --title "RSocketland"
  ```
  
  It should return
  ```
  {"imdb":"aaa","title":"RSocketland","lastModifiedDate":"2020-07-20T12:43:39.857248","likes":0,"dislikes":0}
  ```
  
  A `+` action should be displayed in `movie-client-ui`
  
- Add a movie using REST
  ```
  add-movie-rest --imdb bbb --title "I, REST"
  ```
  
  It should return
  ```
  {"imdb":"bbb","title":"I, REST","lastModifiedDate":"2020-07-20T12:44:13.266657","likes":0,"dislikes":0}
  ```
  
  A `+` action should be displayed in `movie-client-ui`
  
- Send a like to `RSocketland` movie using RSocket (`Fire-And-Forget`)
  ```
  like-movie-rsocket --imdb aaa
  ```
  
  It should return
  ```
  Like submitted
  ```
  
  A `thumbs-up` action should be displayed in `movie-client-ui`

- Get all movies using RSocket (`Request-Stream`)
  ```
  get-movies-rsocket
  ```
  
  It should return
  ```
  {"imdb":"aaa","title":"RSocketland","lastModifiedDate":"2020-07-20T12:56:34.565","likes":1,"dislikes":0}
  {"imdb":"bbb","title":"I, REST","lastModifiedDate":"2020-07-20T12:56:26.846","likes":0,"dislikes":0}
  ```
  
- Select movies using RSocket (`Channel`)
  ```
  select-movies-rsocket --imdbs aaa,bbb
  ```
  
  It should return
  ```
  | IMBD: aaa        | TITLE: RSocketland                    | LIKES: 1     | DISLIKES: 0     |
  | IMBD: bbb        | TITLE: I, REST                        | LIKES: 0     | DISLIKES: 0     |
  ```
  
- Delete movie `RSocketland` using RSocket (`Request-Response`) and movie `I, REST` using REST
  ```
  delete-movie-rsocket --imdb aaa
  delete-movie-rest --imdb bbb
  ```
  
  It should return, as response, the IMDB of the movies
  
  A `-` actions should be displayed in `movie-client-ui`
  
- **Simulation**

  There are two scripts that contain some commands to add, retrieve, like, dislikes and delete movies. One uses REST and another RSocket to communicate with `movie-server`. At the end of the script execution, it's shown the `Execution Time` in `milliseconds`.
  
  - If you are running the applications with Maven
    - REST
      ```
      script ../src/main/resources/simulation-rest.txt
      ```
    - RSocket
      ```
      script ../src/main/resources/simulation-rsocket.txt
      ```

  - If you are running the applications as Docker containers
    - REST
      ```
      script /app/resources/simulation-rest.txt
      ```
    - RSocket
      ```
      script /app/resources/simulation-rsocket.txt
      ```

## Useful Commands

- **MongoDB**

  Find all movies
  ```
  docker exec -it mongodb mongosh moviedb
  db.movies.find()
  ```
  > Type `exit` to get out of `MongoDB` shell

## Shutdown

- To stop `movie-client-shell`, go to the terminal where it is running and type `exit`
- To stop `movie-server` and `movie-client-ui`
  - If you start them with Maven, go to the terminals where they are running and press `Ctrl+C`
  - If you start them as Docker containers, go to a terminal and, inside `springboot-rsocket-webflux-aop` root folder, run the following command
    ```
    ./stop-server-and-ui.sh
    ```
- To stop and remove docker-compose `mongodb` container, network and volumes, go to a terminal and, inside `springboot-rsocket-webflux-aop` root folder, run the command below
  ```
  docker-compose down -v
  ```

## Cleanup

To remove the Docker images created by this project, go to a terminal and, inside `springboot-rsocket-webflux-aop` root folder, run the script below
```
./remove-docker-images.sh
```

## References

- https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket
- https://spring.io/blog/2020/05/12/getting-started-with-rsocket-servers-calling-clients
- https://grapeup.com/blog/reactive-service-to-service-communication-with-rsocket-introduction/

## Issues

#### movie-server

Docker native image builds and starts up successfully. However, it doesn't accept connections even using JVM version of `movie-client-ui` and `movie-client-shell`.  

#### movie-client-ui

After building and starting the Docker native image successfully, the app looks fine (i.e., we can open the page, websocket is connected, etc). However, it is not connecting to `movie-server`.

#### movie-client-shell

After building the Docker native image and running it successfully, we have some problems such as
- there is the following WARN
  ```
  Unable to create a system terminal, creating a dumb terminal (enable debug logging for more information)
  ```
- the autocomplete is not working;
- when tried to run one of script files (`simulation-rest.txt` or `simulation-rsocket.txt`) there is the following exception
  ```
  movie-client-shell> script /app/resources/simulation-rest.txt
  Failed to convert from type [java.util.ArrayList<?>] to type [java.io.File] for value '[/app/resources/simulation-rest.txt]'
  Details of the error have been omitted. You can use the stacktrace command to print the full stacktrace.
  movie-client-shell> stacktrace
  org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.util.ArrayList<?>] to type [java.io.File] for value '[/app/resources/simulation-rest.txt]'
  	at org.springframework.core.convert.support.ConversionUtils.invokeConverter(ConversionUtils.java:47)
  	at org.springframework.core.convert.support.GenericConversionService.convert(GenericConversionService.java:192)
  	at org.springframework.core.convert.support.GenericConversionService.convert(GenericConversionService.java:175)
  	at org.springframework.shell.command.CommandParser$DefaultCommandParser.convertOptionType(CommandParser.java:294)
  	at org.springframework.shell.command.CommandParser$DefaultCommandParser.lambda$parse$4(CommandParser.java:274)
  	at java.base@17.0.6/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
  	at java.base@17.0.6/java.util.ArrayList.forEach(ArrayList.java:1511)
  	at java.base@17.0.6/java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
  	at java.base@17.0.6/java.util.stream.Sink$ChainedReference.end(Sink.java:258)
  	at java.base@17.0.6/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:510)
  	at java.base@17.0.6/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
  	at java.base@17.0.6/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
  	at java.base@17.0.6/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
  	at java.base@17.0.6/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
  	at java.base@17.0.6/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:596)
  	at org.springframework.shell.command.CommandParser$DefaultCommandParser.parse(CommandParser.java:249)
  	at org.springframework.shell.command.CommandExecution$DefaultCommandExecution.evaluate(CommandExecution.java:126)
  	at org.springframework.shell.Shell.evaluate(Shell.java:246)
  	at org.springframework.shell.Shell.run(Shell.java:158)
  	at org.springframework.shell.jline.InteractiveShellRunner.run(InteractiveShellRunner.java:73)
  	at org.springframework.shell.DefaultShellApplicationRunner.run(DefaultShellApplicationRunner.java:65)
  	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:760)
  	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:750)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:317)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1304)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1293)
  	at com.ivanfranchin.movieclientshell.MovieClientShellApplication.main(MovieClientShellApplication.java:10)
  Caused by: org.springframework.core.convert.ConverterNotFoundException: No converter found capable of converting from type [java.lang.String] to type [java.io.File]
  	at org.springframework.core.convert.support.GenericConversionService.handleConverterNotFound(GenericConversionService.java:322)
  	at org.springframework.core.convert.support.GenericConversionService.convert(GenericConversionService.java:195)
  	at org.springframework.core.convert.support.CollectionToObjectConverter.convert(CollectionToObjectConverter.java:66)
  	at org.springframework.core.convert.support.ConversionUtils.invokeConverter(ConversionUtils.java:41)
  	... 26 more
  ```
