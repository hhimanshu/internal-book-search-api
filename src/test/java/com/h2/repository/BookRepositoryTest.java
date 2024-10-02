package com.h2.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.h2.entity.Book;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testAllBooks() {
        List<Book> books = bookRepository.findAll();
        assertNotNull(books);
        System.out.println(String.format("Number of books: %d", books.size()));
        assertEquals(20000, books.size());
    }

    @Test
    public void testBookById() {
        Book book = bookRepository.findById(1L).orElse(null);
        System.out.println(String.format("Book: %s", book.toString()));
        assertNotNull(book);
        assertNotNull(book.getTitle());
        assertNotNull(book.getRating());
        assertNotNull(book.getDescription());
        assertNotNull(book.getLanguage());
        assertNotNull(book.getIsbn());
        assertNotNull(book.getBookFormat());
        assertNotNull(book.getEdition());
        assertNotNull(book.getPages());
        assertNotNull(book.getPublisher());
        assertNotNull(book.getPublishDate());
        assertNotNull(book.getFirstPublishDate());
        assertNotNull(book.getLikedPercent());
        assertNotNull(book.getPrice());
    }
}