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

- [ ] Design the book schema
  - [ ] Identify necessary tables (e.g., books, authors, categories)
  - [ ] Define relationships between tables
  - [ ] Determine primary and foreign keys
- [ ] Create SQL scripts for schema setup
  - [ ] Write CREATE TABLE statements
  - [ ] Add necessary constraints and indexes
- [ ] Implement full-text search in PostgreSQL
  - [ ] Research PostgreSQL full-text search capabilities
  - [ ] Create necessary indexes for full-text search
  - [ ] Write sample full-text search queries
- [ ] Test full-text search functionality
  - [ ] Insert sample data
  - [ ] Run and verify search queries

## Module 5: Ingesting and Validating Data

- [ ] Choose a public archive for book data
  - [ ] Research available options (e.g., Project Gutenberg, Open Library)
  - [ ] Determine data format and structure
- [ ] Write a script to download data
  - [ ] Implement data fetching logic
  - [ ] Handle pagination if necessary
- [ ] Develop a data ingestion script
  - [ ] Parse downloaded data
  - [ ] Map data to database schema
  - [ ] Implement batch insert for efficiency
- [ ] Implement data validation
  - [ ] Check for required fields
  - [ ] Validate data types and formats (e.g., ISBN)
- [ ] Create SQL queries for data integrity checks
  - [ ] Write queries to verify record counts
  - [ ] Implement checks for data consistency

## Module 6: Implementing Business Logic and Writing Tests

- [ ] Define core business logic functions
  - [ ] Implement book search functionality
  - [ ] Create methods for filtering and sorting results
- [ ] Set up JUnit for testing
  - [ ] Add JUnit dependency to `pom.xml`
  - [ ] Create a test directory structure
- [ ] Write unit tests
  - [ ] Create test cases for each business logic function
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
