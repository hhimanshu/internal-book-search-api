package com.h2;

import java.io.*;
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

        while ((line = reader.readLine()) != null) {
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
            String insertBookSQL = "INSERT INTO books (title, series, rating, description, language, isbn, book_format, edition, pages, publisher, publish_date, first_publish_date, liked_percent, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING book_id";
            PreparedStatement insertBookStmt = conn.prepareStatement(insertBookSQL);

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
                    if (record.length < 19) {
                        System.err.println("Skipping malformed record: " + Arrays.toString(record));
                        continue;
                    }

                    // Map CSV columns to variables
                    String title = record[1];
                    String series = record[2];
                    String authorsStr = record[3];
                    String ratingStr = record[4];
                    String description = record[5];
                    String language = record[6];
                    String isbn = record[7];
                    String bookFormat = record[8];
                    String edition = record[9];
                    String pagesStr = record[10];
                    String publisher = record[11];
                    String publishDateStr = record[12];
                    String firstPublishDateStr = record[13];
                    String likedPercentStr = record[14];
                    String priceStr = record[18];

                    // Convert data types
                    Double rating = ratingStr.isEmpty() ? null : Double.parseDouble(ratingStr);
                    Integer pages = pagesStr.isEmpty() ? null : Integer.parseInt(pagesStr);
                    java.sql.Date publishDate = publishDateStr.isEmpty() ? null : java.sql.Date.valueOf(publishDateStr);
                    java.sql.Date firstPublishDate = firstPublishDateStr.isEmpty() ? null : java.sql.Date.valueOf(firstPublishDateStr);
                    Double likedPercent = likedPercentStr.isEmpty() ? null : Double.parseDouble(likedPercentStr);
                    Double price = priceStr.isEmpty() ? null : Double.parseDouble(priceStr);

                    // Insert book
                    insertBookStmt.setString(1, title);
                    insertBookStmt.setString(2, series);
                    insertBookStmt.setObject(3, rating);
                    insertBookStmt.setString(4, description);
                    insertBookStmt.setString(5, language);
                    insertBookStmt.setString(6, isbn);
                    insertBookStmt.setString(7, bookFormat);
                    insertBookStmt.setString(8, edition);
                    insertBookStmt.setObject(9, pages);
                    insertBookStmt.setString(10, publisher);
                    insertBookStmt.setObject(11, publishDate);
                    insertBookStmt.setObject(12, firstPublishDate);
                    insertBookStmt.setObject(13, likedPercent);
                    insertBookStmt.setObject(14, price);

                    ResultSet bookRs = insertBookStmt.executeQuery();
                    int bookId = 0;
                    if (bookRs.next()) {
                        bookId = bookRs.getInt("book_id");
                    }

                    // Insert authors
                    String[] authors = authorsStr.split(",");
                    for (String authorName : authors) {
                        authorName = authorName.trim();
                        // Insert author if not exists
                        insertAuthorStmt.setString(1, authorName);
                        insertAuthorStmt.executeUpdate();
                        // Retrieve author_id
                        selectAuthorStmt.setString(1, authorName);
                        ResultSet authorRs = selectAuthorStmt.executeQuery();
                        int authorId = 0;
                        if (authorRs.next()) {
                            authorId = authorRs.getInt("author_id");
                        }
                        // Insert into book_authors
                        insertBookAuthorStmt.setInt(1, bookId);
                        insertBookAuthorStmt.setInt(2, authorId);
                        insertBookAuthorStmt.executeUpdate();
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