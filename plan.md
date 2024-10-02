# Course Project ToDo List

## Resources

- [Best Books Ever Dataset](https://github.com/scostap/goodreads_bbe_dataset/blob/main/Best_Books_Ever_dataset/books_1.Best_Books_Ever.csv)
  ```
  "bookId","title","series","author","rating","description","language","isbn","genres","characters","bookFormat","edition","pages","publisher","publishDate","firstPublishDate","awards","numRatings","ratingsByStars","likedPercent","setting","coverImg","bbeScore","bbeVotes","price"
  ```

## Module 2: Setting Up Java, Maven, and Blade

- [x] Download and install the latest Java Development Kit (JDK)
  - [x] Verify installation by running `java -version` in the terminal
  - [x] How to manage multiple java versions
- [x] Install Maven
  - [x] Add Maven to system PATH
  - [x] Verify installation by running `mvn -version` in the terminal
- [x] Create a new Maven project
  - x] Run `mvn archetype:generate -DgroupId=com.example -DartifactId=book-search-api -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`
  - [x] Navigate to the project directory: `cd book-search-api`
- [x] Add Sprign Boot framework dependency to `pom.xml`
- [x] Implement a "Hello World" API endpoint
- [x] Test the API
  - [x] Run the application
  - [x] Open in the browser or use cURL to test the endpoint

## Module 3: Dockerizing the Project with PostgreSQL

- [x] Install Docker
  - [x] Download Docker Desktop from the official website
  - [x] Install and start Docker
  - [x] Verify installation by running `docker --version` in the terminal
- [x] Create a `docker-compose.yml` file
  - [x] Define services for the Java application and PostgreSQL
- [x] Test the connection between the Java application and PostgreSQL

## Module 4: Designing the Database Schema and Implementing Full-Text Search

- [x] Design the book schema
  - [x] Identify necessary tables (e.g., books, authors, categories)
  - [x] Define relationships between tables
  - [x] Determine primary and foreign keys
- [x] Create SQL scripts for schema setup
  - [x] Write CREATE TABLE statements
  - [x] Add necessary constraints and indexes
- [x] Implement full-text search in PostgreSQL
  - [x] Research PostgreSQL full-text search capabilities
  - [x] Create necessary indexes for full-text search
  - [x] Write sample full-text search queries
- [x] Test full-text search functionality
  - [x] Insert sample data
  - [x] Run and verify search queries

## Module 5: Ingesting and Validating Data

- [x] Choose a public archive for book data
  - [x] Research available options (e.g., Project Gutenberg, Open Library)
  - [x] Determine data format and structure
- [x] Write a script to download data
  - [x] Implement data fetching logic
  - [x] Handle pagination if necessary
- [x] Develop a data ingestion script
  - [x] Parse downloaded data
  - [x] Map data to database schema
- [x] Implement data validation
  - [x] Check for required fields
  - [ ] Validate data types and formats (e.g., ISBN)
- [x] Create SQL queries for data integrity checks
  - [x] Write queries to verify record counts
  - [x] Implement checks for data consistency

## Module 6: Implementing Business Logic and Writing Tests

> Write persistence layer with tests
> Implement business logic functions with tests

- [x] Define core business logic functions
  - [x] Implement book search functionality
  - [ ] Get Books by Publisher
  - [ ] Get Books by Author
- [x] Set up JUnit for testing
  - [x] Add JUnit dependency to `pom.xml`
  - [x] Create a test directory structure
- [x] Write unit tests
  - [x] Create test cases for each business logic function
  - [ ] Implement edge case tests
- [ ] Run tests and refine code
  - [ ] Execute test suite
  - [ ] Analyze test results and fix any issues

## Module 7: Designing and Creating APIs

- [ ] Design RESTful API endpoints
  - [ ] Define endpoints for searching, filtering, and retrieving books
  - [ ] Determine request and response formats
- [ ] Document API design
  - [ ] Create an API specification document
- [ ] Implement API endpoints using Blade
  - [ ] Create controller classes for each endpoint
  - [ ] Implement request handling and response generation
- [ ] Add input validation and error handling
  - [ ] Implement request parameter validation
  - [ ] Create meaningful error responses
- [ ] Set up integration testing
  - [ ] Choose an integration testing framework
  - [ ] Set up test environment with in-memory database
- [ ] Write integration tests
  - [ ] Create test cases for each API endpoint
  - [ ] Implement end-to-end test scenarios
- [ ] Perform manual API testing
  - [ ] Use Postman or cURL to test each endpoint
- [ ] Create API documentation
  - [ ] Set up Swagger/OpenAPI
  - [ ] Document each endpoint, including parameters and responses
