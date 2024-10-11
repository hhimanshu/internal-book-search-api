package com.h2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.h2.entity.Book;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublisherControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetBooksByPublisher() {
        String publisherName = "MIT Press";
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/publishers/{name}/books", Book[].class,
                publisherName);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Book[] body = response.getBody();
        assertNotNull(body);
        assertThat(body).hasSize(1988);
    }
}
