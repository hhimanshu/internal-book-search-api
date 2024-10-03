package com.h2.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.h2.repository.AuthorRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorTest {
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testGetAuthorId() {
        Author author = authorRepository.findById(1L).orElse(null);
        System.out.println(String.format("Author: %s", author.toString()));
        assertEquals(1L, author.getAuthorId());
    }

    @Test
    void testGetName() {
        Author author = authorRepository.findById(1L).orElse(null);
        System.out.println(String.format("Author: %s", author.toString()));
        assertNotNull(author.getName());
    }

    @Test
    void testRandomAuthor() {
        long randomBetween1And500 = (int) (Math.random() * 500 + 1);
        Author author = authorRepository.findById(randomBetween1And500).orElse(null);
        System.out.println(String.format("Author: %s", author.toString()));
        assertNotNull(author);
        assertEquals(randomBetween1And500, author.getAuthorId());
        assertNotNull(author.getName());
    }
}
