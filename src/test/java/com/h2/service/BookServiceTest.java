package com.h2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.h2.entity.Book;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void testGetAllBooks() {
        List<Book> books = bookService.getAllBooks();
        assertEquals(20000, books.size());
        /* for (Book book : books) {
            System.out.format("Book: %s\n", book.getTitle());
        } */
    }

    @Test
    void testSearchBooks() {
        List<Book> books = bookService.searchBooks("Algorithms");
        assertTrue(books.size() > 0);
        /* for (Book book : books) {
            System.out.format("Search Result: %s\n", book.getTitle());
        } */
    }
}
