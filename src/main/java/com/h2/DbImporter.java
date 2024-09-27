package com.h2;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class DbImporter {

    // private static final String CSV_URL = "https://raw.githubusercontent.com/scostap/goodreads_bbe_dataset/main/Best_Books_Ever_dataset/books_1.Best_Books_Ever.csv";
    private static final String CSV_URL = "https://gist.github.com/hhimanshu/8b51f9cd92bee82d8878dbb9c48a388b/raw/342a95aa594349414c069e2ca74faabb01382213/books_20k.csv";
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

            // Step 2.5: Print all records
            System.out.println("Printing all records...");
            for (String[] record : records) {
                System.out.println(Arrays.toString(record));
            }

            // Step 3: Insert Data into Database
            // System.out.println("Inserting data into database...");
            // insertData(records);

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
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        while ((line = reader.readLine()) != null) {
            List<String> fields = new ArrayList<>();
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    fields.add(matcher.group(1));
                } else {
                    fields.add(matcher.group(2));
                }
            }
            records.add(fields.toArray(new String[0]));
        }
        reader.close();
        return records;
    }

    // Insert data into the database
    private static void insertData(List<String[]> records) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false);

        try {
            System.out.println("Connected to the database.");

            // Prepare SQL statements
            String insertBookSQL = "INSERT INTO books (title, series, rating, description, language, isbn, book_format, edition, pages, publisher, publish_date, first_publish_date, liked_percent, cover_img, bbe_score, bbe_votes, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING book_id";
            PreparedStatement insertBookStmt = conn.prepareStatement(insertBookSQL);

            String insertAuthorSQL = "INSERT INTO authors (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
            PreparedStatement insertAuthorStmt = conn.prepareStatement(insertAuthorSQL);

            String selectAuthorSQL = "SELECT author_id FROM authors WHERE name = ?";
            PreparedStatement selectAuthorStmt = conn.prepareStatement(selectAuthorSQL);

            String insertBookAuthorSQL = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
            PreparedStatement insertBookAuthorStmt = conn.prepareStatement(insertBookAuthorSQL);

            // Similar prepared statements for genres, characters, awards, settings, and ratings

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
                    String coverImg = record[15];
                    String bbeScoreStr = record[16];
                    String bbeVotesStr = record[17];
                    String priceStr = record[18];

                    // Convert data types
                    Double rating = ratingStr.isEmpty() ? null : Double.parseDouble(ratingStr);
                    Integer pages = pagesStr.isEmpty() ? null : Integer.parseInt(pagesStr);
                    java.sql.Date publishDate = publishDateStr.isEmpty() ? null : java.sql.Date.valueOf(publishDateStr);
                    java.sql.Date firstPublishDate = firstPublishDateStr.isEmpty() ? null : java.sql.Date.valueOf(firstPublishDateStr);
                    Double likedPercent = likedPercentStr.isEmpty() ? null : Double.parseDouble(likedPercentStr);
                    Integer bbeScore = bbeScoreStr.isEmpty() ? null : Integer.parseInt(bbeScoreStr);
                    Integer bbeVotes = bbeVotesStr.isEmpty() ? null : Integer.parseInt(bbeVotesStr);
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
                    insertBookStmt.setString(14, coverImg);
                    insertBookStmt.setObject(15, bbeScore);
                    insertBookStmt.setObject(16, bbeVotes);
                    insertBookStmt.setObject(17, price);

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

                    // Similar operations for genres, characters, awards, settings, and ratings

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