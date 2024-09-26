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
    cover_img VARCHAR(255),
    bbe_score INT,
    bbe_votes INT,
    price DECIMAL(10, 2)
);

-- Authors Table
CREATE TABLE authors (
    author_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_authors (
    book_id INT REFERENCES books(book_id),
    author_id INT REFERENCES authors(author_id),
    PRIMARY KEY (book_id, author_id)
);

-- Genres Table
CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_genres (
    book_id INT REFERENCES books(book_id),
    genre_id INT REFERENCES genres(genre_id),
    PRIMARY KEY (book_id, genre_id)
);

-- Characters Table
CREATE TABLE characters (
    character_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_characters (
    book_id INT REFERENCES books(book_id),
    character_id INT REFERENCES characters(character_id),
    PRIMARY KEY (book_id, character_id)
);

-- Awards Table
CREATE TABLE awards (
    award_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_awards (
    book_id INT REFERENCES books(book_id),
    award_id INT REFERENCES awards(award_id),
    PRIMARY KEY (book_id, award_id)
);

-- Settings Table
CREATE TABLE settings (
    setting_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_settings (
    book_id INT REFERENCES books(book_id),
    setting_id INT REFERENCES settings(setting_id),
    PRIMARY KEY (book_id, setting_id)
);

-- Ratings Table
CREATE TABLE ratings (
    rating_id SERIAL PRIMARY KEY,
    book_id INT REFERENCES books(book_id),
    num_ratings INT,
    ratings_by_stars JSONB
);