package com.h2.controller;

import com.h2.entity.Book;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetAllBooks() {
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/books", Book[].class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        assertThat(response.getBody()).hasSize(20000);
    }

    @Test
    void testGetBookById() {
        Long bookId = 1L;
        ResponseEntity<Book> response = restTemplate.getForEntity("/books/{id}", Book.class, bookId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Book body = response.getBody();
        assertNotNull(body);
        assertThat(body.getBookId()).isEqualTo(bookId);
        assertThat(body.getTitle()).isNotBlank();
    }

    @Test
    void testSearchBooks() {
        String searchTerm = "Algorithms";
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/books/search?title={title}", Book[].class,
                searchTerm);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Book[] books = response.getBody();
        assertNotNull(books);
        assertThat(books).hasSizeGreaterThan(0);
        Arrays.stream(books).forEach(book -> assertThat(book.getTitle()).containsIgnoringCase(searchTerm));
        System.out.println("Total books found for 'Algorithms': " + books.length);
    }
}
