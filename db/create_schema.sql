-- Books Table
CREATE TABLE books (
    book_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    series VARCHAR(255),
    rating DECIMAL(3, 2),
    description TEXT,
    language VARCHAR(50),
    isbn VARCHAR(20),
    book_format VARCHAR(50),
    edition VARCHAR(50),
    pages INT,
    publisher VARCHAR(255),
    publish_date DATE,
    first_publish_date DATE,
    liked_percent DECIMAL(5, 2),
    price DECIMAL(10, 2)
);

-- Authors Table
CREATE TABLE authors (
    author_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_authors (
    book_id INT REFERENCES books (book_id),
    author_id INT REFERENCES authors (author_id),
    PRIMARY KEY (book_id, author_id)
);