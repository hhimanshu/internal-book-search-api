## Module 2: Setting Up Java, Maven, and Blade

### Install Java Development Kit (JDK)

- Install [SDKMAN](https://sdkman.io/)
  ```
  curl -s "https://get.sdkman.io" | bash
  ```
- Open new tab
- Run following command
  ```
  sdk version
  ```
- List available Java versions from Open
  ```
  (base) ➜  ~ sdk list java | grep -i open
  ```
  You should see a list similar to this:
  ```
  Java.net      |     | 24.ea.16     | open    |            | 24.ea.16-open
               |     | 24.ea.15     | open    |            | 24.ea.15-open
               |     | 24.ea.14     | open    |            | 24.ea.14-open
               |     | 24.ea.13     | open    |            | 24.ea.13-open
               |     | 24.ea.12     | open    |            | 24.ea.12-open
               |     | 24.ea.11     | open    |            | 24.ea.11-open
               |     | 24.ea.10     | open    |            | 24.ea.10-open
               |     | 24.ea.9      | open    |            | 24.ea.9-open
               |     | 23.ea.29     | open    |            | 23.ea.29-open
               |     | 22.0.1       | open    |            | 22.0.1-open
               |     | 21.0.2       | open    |            | 21.0.2-open
  ```
- Install specific version of Java
  ```
  sdk install java 24.ea.16-open
  sdk install java 24.ea.15-open
  ```
- Use a specific version of Java
  ```
  sdk use java 24.ea.16-open
  sdk use java 24.ea.15-open
  ```
- Verify installation by running `java -version` in the terminal

### Install Maven

- Install [Maven](https://maven.apache.org/install.html)
  ```
  brew install maven
  ```

```
brew install maven
```

- Verify installation by running `mvn -version` in the terminal

### Create a new Maven project

- Crete the project

```
mvn archetype:generate -DgroupId=com.h2 -DartifactId=book-search-api -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

- Ensure project works

```
cd book-search-api && mvn clean install
```

## Module 3: Dockerizing the Project with PostgreSQL

- Start container

  ```
  docker-compose up -d
  ```

- Connect to database
  ```
  docker exec -it library-db psql -U admin -d library
  ```
- Verify version

```
SELECT version();
```

- List databases

```
\l
```

- Stop container

  ```
  docker-compose down
  ```

- Test the connection between the Java application and PostgreSQL

```
mvn spring-boot:run
```

This should print the PostgreSQL version in the console. This is because of the following reasoning

```
The DatabaseTestRunner class is running because it implements the CommandLineRunner interface and is annotated with @Component. In Spring Boot, any bean that implements CommandLineRunner will be executed after the application context is loaded.

Explanation
@Component Annotation: The @Component annotation marks the DatabaseTestRunner class as a Spring bean. This means it will be automatically detected and registered by Spring's component scanning.

CommandLineRunner Interface: The CommandLineRunner interface has a single method, run(String... args), which is called just before the Spring Boot application finishes starting up. This allows you to run additional code after the application context is loaded.

How It Works
When you run mvn spring-boot:run, Spring Boot starts the application.
During the startup process, Spring Boot scans for components and finds the DatabaseTestRunner class because it is annotated with @Component.
Spring Boot creates an instance of DatabaseTestRunner and calls its run method because it implements CommandLineRunner.
The run method executes the SQL query to get the PostgreSQL version and prints it to the console.
```

## Module 4: Designing the Database Schema and Implementing Full-Text Search

- Setup PGAdmin

  ```
  docker pull dpage/pgadmin4
  docker run -p 80:80 \
    -e 'PGADMIN_DEFAULT_EMAIL=user@domain.com' \
    -e 'PGADMIN_DEFAULT_PASSWORD=SuperSecret' \
    -d dpage/pgadmin4
  ```
  Visit `http://localhost:80` in your browser

- Create tables and relationships

```

docker cp db/create_schema.sql library-db:/create_schema.sql
docker exec -it library-db psql -U admin -d library -f /create_schema.sql

```

- Insert sample data

```

docker cp db/insert.sql library-db:/insert.sql
docker exec -it library-db psql -U admin -d library -f /insert.sql

```

```

```
