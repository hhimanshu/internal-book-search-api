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

- full-text search 1
```sql
INSERT INTO books (title, description, isbn, rating, language, book_format, pages, publisher, publish_date)
VALUES 
('Introduction to Algorithms', 'A comprehensive update of the leading algorithms text, with new material on matchings in bipartite graphs, online algorithms, machine learning, and other topics.', '9780262046305', 4.50, 'English', 'Hardcover', 1312, 'MIT Press', '2022-04-05'),
('Clean Code: A Handbook of Agile Software Craftsmanship', 'Even bad code can function. But if code isn''t clean, it can bring a development organization to its knees. This book is a must for any developer, software engineer, project manager, team lead, or systems analyst with an interest in producing better code.', '9780132350884', 4.39, 'English', 'Paperback', 464, 'Prentice Hall', '2008-08-11'),
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Capturing a wealth of experience about the design of object-oriented software, four top-notch designers present a catalog of simple and succinct solutions to commonly occurring design problems.', '9780201633610', 4.19, 'English', 'Hardcover', 395, 'Addison-Wesley Professional', '1994-10-31'),
('The Pragmatic Programmer: Your Journey to Mastery', 'The Pragmatic Programmer is one of those rare tech books you''ll read, re-read, and read again over the years. Whether you''re new to the field or an experienced practitioner, you''ll come away with fresh insights each and every time.', '9780135957059', 4.38, 'English', 'Paperback', 352, 'Addison-Wesley Professional', '2019-09-13');

ALTER TABLE books ADD COLUMN search_vector tsvector;

UPDATE books SET search_vector = 
    setweight(to_tsvector('english', coalesce(title,'')), 'A') ||
    setweight(to_tsvector('english', coalesce(description,'')), 'B') ||
    setweight(to_tsvector('english', coalesce(isbn,'')), 'C');


-- Example 1: to_tsvector
SELECT to_tsvector('english', 'Introduction to Algorithms');

-- Example 2: to_tsvector with a longer text
SELECT to_tsvector('english', 'A comprehensive update of the leading algorithms text, with new material on matchings in bipartite graphs, online algorithms, machine learning, and other topics.');

-- Example 3: to_tsquery with a single word
SELECT to_tsquery('english', 'algorithms');

-- Example 4: to_tsquery with multiple words and operators
SELECT to_tsquery('english', 'algorithms & (graphs | learning)');

-- Example 5: plainto_tsquery
SELECT plainto_tsquery('english', 'introduction to algorithms');

-- Example 6: Comparing to_tsquery and plainto_tsquery
SELECT to_tsquery('english', 'clean & code'), plainto_tsquery('english', 'clean code');

-- Example 7: Using to_tsquery with prefix matching
SELECT to_tsquery('english', 'algorithm:*');

-- Example 8: Demonstrating normalization and stop word removal
SELECT to_tsvector('english', 'The Algorithms are running quickly and efficiently');    


-- Example 1a: Basic search using to_tsquery with a single term
SELECT title, ts_rank(search_vector, to_tsquery('english', 'algorithms')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'algorithms')
ORDER BY rank DESC;

-- Example 1b: Search using to_tsquery with multiple terms (OR)
SELECT title, ts_rank(search_vector, to_tsquery('english', 'algorithms | design')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'algorithms | design')
ORDER BY rank DESC;

-- Example 1c: Search using to_tsquery with multiple terms (AND)
SELECT title, ts_rank(search_vector, to_tsquery('english', 'algorithms & learning')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'algorithms & learning')
ORDER BY rank DESC;

-- Example 1d: Search using to_tsquery with prefix matching
SELECT title, ts_rank(search_vector, to_tsquery('english', 'algorithm:*')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'algorithm:*')
ORDER BY rank DESC;

-- Example 2: Phrase search using phraseto_tsquery
SELECT title, ts_rank(search_vector, phraseto_tsquery('english', 'object-oriented software')) as rank
FROM books
WHERE search_vector @@ phraseto_tsquery('english', 'object-oriented software')
ORDER BY rank DESC;

-- Example 3: Natural language search using plainto_tsquery
SELECT title, ts_rank(search_vector, plainto_tsquery('english', 'clean code software development')) as rank
FROM books
WHERE search_vector @@ plainto_tsquery('english', 'clean code software development')
ORDER BY rank DESC;

-- Example 4: Combining fields in search
SELECT title, ts_rank(search_vector, to_tsquery('english', 'programmer & (mastery | craftsmanship)')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'programmer & (mastery | craftsmanship)')
ORDER BY rank DESC;

-- Example 5: Searching with ISBN
SELECT title, ts_rank(search_vector, plainto_tsquery('english', '9780135957059')) as rank
FROM books
WHERE search_vector @@ plainto_tsquery('english', '9780135957059')
ORDER BY rank DESC;

-- Example 6: Complex query with multiple terms
SELECT title, ts_rank(search_vector, to_tsquery('english', 'design & (patterns | algorithms) & !pragmatic')) as rank
FROM books
WHERE search_vector @@ to_tsquery('english', 'design & (patterns | algorithms) & !pragmatic')
ORDER BY rank DESC;
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