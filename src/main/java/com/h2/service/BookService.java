package com.h2.service;

import com.h2.entity.Author;
import com.h2.entity.Book;
import com.h2.entity.BookAuthor;
import com.h2.repository.AuthorRepository;
import com.h2.repository.BookAuthorRepository;
import com.h2.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return bookRepository.searchBooks(searchTerm);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getBooksByPublisher(String publisher) {
        if (publisher == null || publisher.isEmpty()) {
            throw new IllegalArgumentException("Publisher cannot be empty");
        }
        return bookRepository.getBooksByPublisher(publisher);
    }

    public List<Book> getBooksByAuthor(String author) {
        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        Author authorObj = authorRepository.findByName(author);
        if (authorObj == null) {
            throw new IllegalArgumentException("Author not found");
        }
        System.out.println(String.format("Author: %s", authorObj.toString()));
        List<BookAuthor> bookAuthors = bookAuthorRepository.findByAuthorAuthorId(authorObj.getAuthorId());
        List<Long> bookIds = bookAuthors.stream().map(BookAuthor::getBookId).collect(Collectors.toList());
        return bookRepository.findAllById(bookIds);
    }
}