package com.h2;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.sql.*;
import java.util.*;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class DbImporter {

    private static final String CSV_URL = "https://gist.github.com/hhimanshu/d55d17b51e0a46a37b739d0f3d3e3c74/raw/5b9027cf7b1641546c1948caffeaa44129b7db63/books.csv";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/library";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin123";

    public static void main(String[] args) {
        try {
            System.out.println("Starting data ingestion...");

            // Step 1: Download CSV Data
            System.out.println("Downloading CSV data...");
            InputStream csvStream = downloadCSV(CSV_URL);

            // Step 2: Parse CSV Data
            System.out.println("Parsing CSV data...");
            List<String[]> records = parseCSV(csvStream);

            // Step 3: Insert Data into Database
            System.out.println("Inserting data into database...");
            insertData(records);

            System.out.println("Data ingestion completed successfully.");

        } catch (Exception e) {
            System.err.println("An error occurred during data ingestion:");
            e.printStackTrace();
        }
    }

    // Download the CSV file from the URL
    private static InputStream downloadCSV(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn.getInputStream();
    }

    // Preprocess and parse the CSV file using OpenCSV
    private static List<String[]> parseCSV(InputStream csvStream) throws IOException, CsvValidationException {
        List<String[]> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
        String line;
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        boolean isFirstLine = true;

        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Skip the header row
            }

            if (inQuotes) {
                sb.append("\n").append(line);
                if (line.endsWith("\"")) {
                    inQuotes = false;
                    records.add(parseLine(sb.toString()));
                    sb.setLength(0);
                }
            } else {
                if (line.startsWith("\"") && !line.endsWith("\"")) {
                    inQuotes = true;
                    sb.append(line);
                } else {
                    records.add(parseLine(line));
                }
            }
        }
        reader.close();
        return records;
    }

    // Parse a single line using OpenCSV
    private static String[] parseLine(String line) throws IOException, CsvValidationException {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .withStrictQuotes(false)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        CSVReader csvReader = new CSVReaderBuilder(new StringReader(line))
                .withCSVParser(parser)
                .build();
        return csvReader.readNext();
    }

    // Insert data into the database
    private static void insertData(List<String[]> records) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false);

        try {
            System.out.println("Connected to the database.");

            // Prepare SQL statements
            String insertBookSQL = "INSERT INTO books (title, rating, description, language, isbn, book_format, edition, pages, publisher, publish_date, first_publish_date, liked_percent, price) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                   "ON CONFLICT";

            String insertAuthorSQL = "INSERT INTO authors (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
            PreparedStatement insertAuthorStmt = conn.prepareStatement(insertAuthorSQL);

            String selectAuthorSQL = "SELECT author_id FROM authors WHERE name = ?";
            PreparedStatement selectAuthorStmt = conn.prepareStatement(selectAuthorSQL);

            String insertBookAuthorSQL = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
            PreparedStatement insertBookAuthorStmt = conn.prepareStatement(insertBookAuthorSQL);

            // Process each record
            for (String[] record : records) {
                try {
                    // Log the current record
                    System.out.println("Processing record: " + Arrays.toString(record));

                    // Ensure the record has the expected number of columns
                    if (record.length < 15) {
                        System.err.println("Skipping malformed record: " + Arrays.toString(record));
                        continue;
                    }

                    // Map CSV columns to variables
                    String title = record[1];
                    String authorsStr = record[2];
                    String ratingStr = record[3];
                    String description = record[4];
                    String language = record[5];
                    String isbn = record[6];
                    String bookFormat = record[7];
                    String edition = record[8];
                    String pagesStr = record[9];
                    String publisher = record[10];
                    String publishDateStr = record[11];
                    String firstPublishDateStr = record[12];
                    String likedPercentStr = record[13];
                    String priceStr = record[14];

                    // Convert data types
                    BigDecimal rating = ratingStr.isEmpty() ? null : new BigDecimal(ratingStr);
                    Integer pages = pagesStr.isEmpty() ? null : Integer.parseInt(pagesStr);
                    java.sql.Date publishDate = publishDateStr.isEmpty() ? null : java.sql.Date.valueOf(publishDateStr);
                    java.sql.Date firstPublishDate = firstPublishDateStr.isEmpty() ? null : java.sql.Date.valueOf(firstPublishDateStr);
                    BigDecimal likedPercent = likedPercentStr.isEmpty() ? null : new BigDecimal(likedPercentStr);
                    BigDecimal price = priceStr.isEmpty() ? null : new BigDecimal(priceStr);

                    // Insert book
                    try (PreparedStatement pstmt = conn.prepareStatement(insertBookSQL)) {
                        pstmt.setString(1, title);
                        pstmt.setBigDecimal(2, rating);
                        pstmt.setString(3, description);
                        pstmt.setString(4, language);
                        pstmt.setString(5, isbn); // isbn can be null
                        pstmt.setString(6, bookFormat);
                        pstmt.setString(7, edition);
                        pstmt.setInt(8, pages);
                        pstmt.setString(9, publisher);
                        pstmt.setDate(10, publishDate);
                        pstmt.setDate(11, firstPublishDate);
                        pstmt.setBigDecimal(12, likedPercent);
                        pstmt.setBigDecimal(13, price);
                        pstmt.executeUpdate();
                    }

                    // Retrieve book_id
                    int bookId = 0;
                    if (isbn != null && !isbn.isEmpty()) {
                        // Use isbn to retrieve book_id
                        String selectBookIdSql = "SELECT book_id FROM books WHERE isbn = ?";
                        try (PreparedStatement selectBookIdStmt = conn.prepareStatement(selectBookIdSql)) {
                            selectBookIdStmt.setString(1, isbn);
                            ResultSet bookRs = selectBookIdStmt.executeQuery();
                            if (bookRs.next()) {
                                bookId = bookRs.getInt("book_id");
                            }
                        }
                    } else {
                        // Retrieve the last inserted book_id for entries without isbn
                        String selectBookIdSql = "SELECT currval(pg_get_serial_sequence('books', 'book_id')) AS book_id";
                        try (Statement stmt = conn.createStatement()) {
                            ResultSet bookRs = stmt.executeQuery(selectBookIdSql);
                            if (bookRs.next()) {
                                bookId = bookRs.getInt("book_id");
                            }
                        }
                    }

                    // Insert authors
                    String[] authors = authorsStr.split(",");
                    for (String authorName : authors) {
                        authorName = authorName.trim();
                        // Insert author
                        insertAuthorStmt.setString(1, authorName);
                        insertAuthorStmt.executeUpdate();
                        // Retrieve author_id
                        selectAuthorStmt.setString(1, authorName);
                        try (ResultSet authorRs = selectAuthorStmt.executeQuery()) {
                            int authorId = 0;
                            if (authorRs.next()) {
                                authorId = authorRs.getInt("author_id");
                            }
                            // Insert into book_authors
                            insertBookAuthorStmt.setInt(1, bookId);
                            insertBookAuthorStmt.setInt(2, authorId);
                            insertBookAuthorStmt.executeUpdate();
                        }
                    }

                    // Commit transaction after each record
                    conn.commit();
                } catch (SQLException e) {
                    System.err.println("Failed to process record: " + Arrays.toString(record));
                    e.printStackTrace();
                    conn.rollback();
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error:");
            e.printStackTrace();
            conn.rollback();
        } finally {
            conn.close();
        }
    }
}