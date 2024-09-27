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

### Query the database

- Case insensitive search

```
SELECT *
FROM books
WHERE title ILIKE '%hunger games%'
   OR description ILIKE '%hunger games%';
```
- Discuss the limitations of case-insensitive search
1. Case-insensitive search is not efficient for large datasets.
2. It does not handle word variations (e.g., "game" vs. "games").
3. It does not support partial word matches (e.g., "hunger" vs. "hunger games").
4. It does not consider the relevance of search results.
5. It does not provide advanced search features like phrase matching or stemming.

- full-text search
```sql
truncate table books cascade;

-- Ensure we have our books table with some relevant data
INSERT INTO books (title, series, description) VALUES
('Dystopian Worlds', NULL, 'A comprehensive guide to dystopian literature including works like 1984 and Brave New World.'),
('The Hunger Games Trilogy', 'The Hunger Games', 'The complete trilogy of The Hunger Games, Catching Fire, and Mockingjay.'),
('Future Societies', NULL, 'An analysis of fictional future societies in literature, from utopias to dystopias.'),
('Games and Survival', NULL, 'Exploring the theme of survival games in modern fiction, including Battle Royale and The Hunger Games.');

-- Ensure the search vector is updated
UPDATE books SET search_vector =
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(series, '')), 'B') ||
    setweight(to_tsvector('english', coalesce(description, '')), 'C');

-- Example 1: Using to_tsquery
SELECT book_id, ts_rank(search_vector, to_tsquery('english', 'dystopian & (future | world)')) AS rank, title, series, description
FROM books
WHERE search_vector @@ to_tsquery('english', 'dystopian & (future | world)')
ORDER BY rank DESC;

-- Example 2: Using phraseto_tsquery
SELECT book_id, ts_rank(search_vector, phraseto_tsquery('english', 'Hunger Games')) AS rank, title, series, description
FROM books
WHERE search_vector @@ phraseto_tsquery('english', 'Hunger Games')
ORDER BY rank DESC;

-- Example 3: Using plainto_tsquery
SELECT book_id, ts_rank(search_vector, plainto_tsquery('english', 'future society dystopia')) AS rank, title, series, description
FROM books
WHERE search_vector @@ plainto_tsquery('english', 'future society dystopia')
ORDER BY rank DESC;

-- Demonstrating the difference in query interpretation
SELECT to_tsquery('english', 'dystopian & (future | world)') AS to_tsquery_result,
       phraseto_tsquery('english', 'Hunger Games') AS phraseto_tsquery_result,
       plainto_tsquery('english', 'future society dystopia') AS plainto_tsquery_result;
```

- Ignore this
```sql
-- First, let's add more varied data to our books table
INSERT INTO books (title, series, description) VALUES
('The Hunger Games', 'The Hunger Games #1', 'In a dystopian future, Katniss Everdeen voluntarily takes her younger sister''s place in the Hunger Games.'),
('Catching Fire', 'The Hunger Games #2', 'Katniss Everdeen and Peeta Mellark become targets of the Capitol after their victory in the 74th Hunger Games.'),
('Mockingjay', 'The Hunger Games #3', 'Katniss Everdeen reluctantly becomes the symbol of a mass rebellion against the autocratic Capitol.'),
('The Ballad of Songbirds and Snakes', 'The Hunger Games #0', 'A prequel to the Hunger Games series, focusing on Coriolanus Snow''s youth.'),
('1984', NULL, 'A dystopian novel by George Orwell about a totalitarian future society.'),
('Brave New World', NULL, 'Aldous Huxley''s novel about a futuristic World State and its citizens.'),
('The Giver', 'The Giver Quartet #1', 'In a seemingly perfect community, a boy is chosen to inherit the position of Receiver of Memories.'),
('Divergent', 'Divergent #1', 'In a dystopian Chicago, society is divided into five factions. Beatrice Prior must choose her faction.'),
('Ready Player One', NULL, 'In 2045, people seek escape from reality through the virtual reality world OASIS.');

-- Update the search vector for all books
UPDATE books SET search_vector =
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(series, '')), 'B') ||
    setweight(to_tsvector('english', coalesce(description, '')), 'C');

-- Example 1: Basic to_tsquery
SELECT ts_rank(search_vector, to_tsquery('english', 'hunger & games')) AS rank, title, series, description
FROM books
WHERE search_vector @@ to_tsquery('english', 'hunger & games')
ORDER BY rank DESC;

-- Example 2: phraseto_tsquery
SELECT ts_rank(search_vector, phraseto_tsquery('english', 'Hunger Games')) AS rank, title, series, description
FROM books
WHERE search_vector @@ phraseto_tsquery('english', 'Hunger Games')
ORDER BY rank DESC;

-- Example 3: Complex to_tsquery
SELECT ts_rank(search_vector, to_tsquery('english', 'dystopian & !hunger')) AS rank, title, series, description
FROM books
WHERE search_vector @@ to_tsquery('english', 'dystopian & !hunger')
ORDER BY rank DESC;

-- Example 4: Demonstrating how ts_rank works with field weights
SELECT
       ts_rank(search_vector, to_tsquery('english', 'dystopian')) AS default_rank,
       ts_rank('{0.1, 0.2, 0.4, 1.0}', search_vector, to_tsquery('english', 'dystopian')) AS custom_rank,
	   title, series, description
FROM books
WHERE search_vector @@ to_tsquery('english', 'dystopian')
ORDER BY custom_rank DESC;

-- Example 5: Using plainto_tsquery for natural language input
SELECT ts_rank(search_vector, plainto_tsquery('english', 'dystopian future')) AS rank, title, series, description
FROM books
WHERE search_vector @@ plainto_tsquery('english', 'dystopian future')
ORDER BY rank DESC;

-- How query works
SELECT plainto_tsquery('english', 'dystopian future');
````

## Module 5: Ingesting and Validating Data
- Query after ingestion
```sql
select count(*) from authors;
select count(*) from books;
select count(*) from book_authors;

SELECT b.*, a."name" as author_name
FROM books b
JOIN book_authors ba ON b.book_id = ba.book_id
JOIN authors a ON ba.author_id = a.author_id
WHERE a.name = 'Steve Wozniak'; -- Sundar Pichai
```