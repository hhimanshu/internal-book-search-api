package com.h2.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.h2.repository.BookAuthorRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookAuthorTest {

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Test
    void testTotalBookAuthors() {
        long totalBookAuthors = bookAuthorRepository.count();
        assertEquals(20000L, totalBookAuthors);
    }
}
