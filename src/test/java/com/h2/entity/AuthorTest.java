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
        Author author = authorRepository.findById(40L).orElse(null);
        System.out.println(String.format("Author: %s", author.toString()));
        assertNotNull(author.getName());
    }

    @Test
    void testRandomAuthor() {
        long randomAuthorId = (long) (Math.random() * 9 + 1);
        System.out.println(String.format("Random Author ID: %d", randomAuthorId));
        Author author = authorRepository.findById(randomAuthorId).orElse(null);
        System.out.println(String.format("Author: %s", author.toString()));
        assertNotNull(author);
        assertEquals(randomAuthorId, author.getAuthorId());
        assertNotNull(author.getName());
    }
}
