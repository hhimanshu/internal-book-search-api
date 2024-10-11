package com.h2.controller;

import com.h2.entity.Book;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetBooksByAuthor() {
        String authorName = "Tim Cook";
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/authors/{name}/books", Book[].class, authorName);

        Book[] body = response.getBody();
        assertNotNull(body);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSizeGreaterThan(0);
        assertThat(response.getBody()).hasSizeBetween(350, 400);
        System.out.println("total books by author: " + body.length);
    }
}
