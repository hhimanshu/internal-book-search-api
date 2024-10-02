package com.h2;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.sql.*;
import java.sql.Date;
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

        String sql = "INSERT INTO books (book_id, title, rating, description, language, isbn, book_format, edition, pages, publisher, publish_date, first_publish_date, liked_percent, price) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] record : records) {
                try {
                    // Set parameters
                    pstmt.setInt(1, Integer.parseInt(record[0])); // book_id
                    pstmt.setString(2, record[1]); // title
                    pstmt.setBigDecimal(3, new BigDecimal(record[3])); // rating
                    pstmt.setString(4, record[4]); // description
                    pstmt.setString(5, record[5]); // language
                    pstmt.setString(6, record[6]); // isbn
                    pstmt.setString(7, record[7]); // book_format
                    pstmt.setString(8, record[8]); // edition
                    pstmt.setInt(9, Integer.parseInt(record[9])); // pages
                    pstmt.setString(10, record[10]); // publisher
                    pstmt.setDate(11, Date.valueOf(record[11])); // publish_date
                    pstmt.setDate(12, Date.valueOf(record[12])); // first_publish_date
                    pstmt.setBigDecimal(13, new BigDecimal(record[13])); // liked_percent
                    pstmt.setBigDecimal(14, new BigDecimal(record[14])); // price

                    pstmt.addBatch();
                } catch (Exception e) {
                    System.err.println("Failed to process record: " + Arrays.toString(record));
                    e.printStackTrace();
                }
            }

            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
}