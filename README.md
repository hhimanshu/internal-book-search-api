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
