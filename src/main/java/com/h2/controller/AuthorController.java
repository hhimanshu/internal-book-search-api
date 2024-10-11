package com.h2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.h2.entity.Book;
import com.h2.service.BookService;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private BookService bookService;

    @GetMapping("/{name}/books")
    public List<Book> getBooksByAuthor(@PathVariable String name) {
        return bookService.getBooksByAuthor(name);
    }
}