# springboot-rsocket-webflux-aop

The goal of this project is to play with [`RSocket`](https://rsocket.io/) protocol. For it, we will implement three [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java applications: `movie-server`, `movie-client-shell` and `movie-client-ui`. As storage, it's used the reactive NoSQL database [`MongoDB`](https://www.mongodb.com/). All the streaming of movie events and the logging are handling by AOP (Aspect Oriented Programming).

## Project Architecture

![project-diagram](documentation/project-diagram.png)

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
  
  ![movie-client-shell](documentation/movie-client-shell.png)

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

  ![movie-client-ui](documentation/movie-client-ui.png)

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

- [`Java 11+`](https://www.oracle.com/java/technologies/downloads/#java11)
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
  docker exec -it mongodb mongo moviedb
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

After building the Docker native image successfully, an exception is thrown at startup time
```
ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'movieRSocketController': Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'movieServiceImpl': Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'movieRepository': Cannot create inner bean '(inner bean)#565cc52a' of type [org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean] while setting bean property 'repositoryFragments'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#565cc52a': Initialization of bean failed; nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.beans.factory.FactoryBean, interface org.springframework.beans.factory.BeanFactoryAware, interface org.springframework.beans.factory.InitializingBean] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolve(InjectedConstructionResolver.java:88) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedElementResolver.resolve(InjectedElementResolver.java:35) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.InjectedElementResolver.create(InjectedElementResolver.java:66) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar$BeanInstanceContext.create(BeanDefinitionRegistrar.java:211) ~[na:na]
	at org.springframework.aot.ContextBootstrapInitializer.lambda$initialize$5(ContextBootstrapInitializer.java:267) ~[na:na]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar$ThrowableFunction.apply(BeanDefinitionRegistrar.java:294) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar.lambda$instanceSupplier$0(BeanDefinitionRegistrar.java:115) ~[na:na]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.obtainFromSupplier(AbstractAutowireCapableBeanFactory.java:1249) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1191) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:955) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext.refresh(ReactiveWebServerApplicationContext.java:66) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:734) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:408) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:308) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1306) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1295) ~[com.ivanfranchin.movieserver.MovieServerApplication:2.7.2]
	at com.ivanfranchin.movieserver.MovieServerApplication.main(MovieServerApplication.java:10) ~[com.ivanfranchin.movieserver.MovieServerApplication:na]
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'movieServiceImpl': Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'movieRepository': Cannot create inner bean '(inner bean)#565cc52a' of type [org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean] while setting bean property 'repositoryFragments'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#565cc52a': Initialization of bean failed; nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.beans.factory.FactoryBean, interface org.springframework.beans.factory.BeanFactoryAware, interface org.springframework.beans.factory.InitializingBean] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolve(InjectedConstructionResolver.java:88) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedElementResolver.resolve(InjectedElementResolver.java:35) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.InjectedElementResolver.create(InjectedElementResolver.java:66) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar$BeanInstanceContext.create(BeanDefinitionRegistrar.java:211) ~[na:na]
	at org.springframework.aot.ContextBootstrapInitializer.lambda$initialize$9(ContextBootstrapInitializer.java:273) ~[na:na]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar$ThrowableFunction.apply(BeanDefinitionRegistrar.java:294) ~[com.ivanfranchin.movieserver.MovieServerApplication:0.12.1]
	at org.springframework.aot.beans.factory.BeanDefinitionRegistrar.lambda$instanceSupplier$0(BeanDefinitionRegistrar.java:115) ~[na:na]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.obtainFromSupplier(AbstractAutowireCapableBeanFactory.java:1249) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1191) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:582) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1391) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1311) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.lambda$resolve$0(InjectedConstructionResolver.java:83) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolveDependency(InjectedConstructionResolver.java:97) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolve(InjectedConstructionResolver.java:83) ~[na:na]
	... 24 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'movieRepository': Cannot create inner bean '(inner bean)#565cc52a' of type [org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean] while setting bean property 'repositoryFragments'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#565cc52a': Initialization of bean failed; nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.beans.factory.FactoryBean, interface org.springframework.beans.factory.BeanFactoryAware, interface org.springframework.beans.factory.InitializingBean] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveInnerBean(BeanDefinitionValueResolver.java:389) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:134) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyPropertyValues(AbstractAutowireCapableBeanFactory.java:1707) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1452) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:619) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1391) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1311) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.lambda$resolve$0(InjectedConstructionResolver.java:83) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolveDependency(InjectedConstructionResolver.java:97) ~[na:na]
	at org.springframework.aot.beans.factory.InjectedConstructionResolver.resolve(InjectedConstructionResolver.java:83) ~[na:na]
	... 44 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#565cc52a': Initialization of bean failed; nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.beans.factory.FactoryBean, interface org.springframework.beans.factory.BeanFactoryAware, interface org.springframework.beans.factory.InitializingBean] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:628) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:542) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveInnerBean(BeanDefinitionValueResolver.java:374) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	... 59 common frames omitted
Caused by: com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.beans.factory.FactoryBean, interface org.springframework.beans.factory.BeanFactoryAware, interface org.springframework.beans.factory.InitializingBean] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
	at com.oracle.svm.core.util.VMError.unsupportedFeature(VMError.java:89) ~[na:na]
	at com.oracle.svm.reflect.proxy.DynamicProxySupport.getProxyClass(DynamicProxySupport.java:158) ~[na:na]
	at java.lang.reflect.Proxy.getProxyConstructor(Proxy.java:48) ~[com.ivanfranchin.movieserver.MovieServerApplication:na]
	at java.lang.reflect.Proxy.getProxyClass(Proxy.java:398) ~[com.ivanfranchin.movieserver.MovieServerApplication:na]
	at org.springframework.util.ClassUtils.createCompositeInterface(ClassUtils.java:784) ~[na:na]
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.getTargetShadowMatch(AspectJExpressionPointcut.java:437) ~[na:na]
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.matches(AspectJExpressionPointcut.java:295) ~[na:na]
	at org.springframework.aop.support.AopUtils.canApply(AopUtils.java:251) ~[na:na]
	at org.springframework.aop.support.AopUtils.canApply(AopUtils.java:289) ~[na:na]
	at org.springframework.aop.support.AopUtils.findAdvisorsThatCanApply(AopUtils.java:321) ~[na:na]
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.findAdvisorsThatCanApply(AbstractAdvisorAutoProxyCreator.java:128) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.findEligibleAdvisors(AbstractAdvisorAutoProxyCreator.java:97) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.getAdvicesAndAdvisorsForBean(AbstractAdvisorAutoProxyCreator.java:78) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.wrapIfNecessary(AbstractAutoProxyCreator.java:341) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessAfterInitialization(AbstractAutoProxyCreator.java:293) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(AbstractAutowireCapableBeanFactory.java:455) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1808) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:620) ~[com.ivanfranchin.movieserver.MovieServerApplication:5.3.22]
	... 61 common frames omitted
```

I've tried to add the not found interface (`org.springframework.beans.factory.FactoryBean`, `org.springframework.beans.factory.BeanFactoryAware` and `org.springframework.beans.factory.InitializingBean`) using `@AotProxyHint` but, the image is not even compiling.

#### movie-client-ui

After building and starting the Docker native image successfully, the app looks fine (i.e., we can open the page, websocket is connecting, etc).

However, after submitting some `add-movie-(rest or rsocket)` commands using JVM version of `movie-client-shell` and `movie-server`, nothing happens in the `movie-client-ui`.

It looks like the endpoint `moviesUpdates`, in `MovieClientUiController` class, is not working in native image.

#### movie-client-shell

After building the Docker native image and running it successfully, we have some problems such as
- there is the following WARN
  ```
  Unable to create a system terminal, creating a dumb terminal (enable debug logging for more information)
  ```
- the autocomplete is not working;
- the script files `simulation-rest.txt` and `simulation-rsocket.txt` are not present.
