package com.h2.repository;

import com.h2.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testFindAll() {
        List<Book> books = bookRepository.findAll();
        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

   /*  @Test
    public void testSaveBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book = bookRepository.save(book);
        assertNotNull(book.getBookId());
    } */
}