package com.h2.controller;

import com.h2.entity.Book;
import com.h2.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {
    @Autowired
    private BookService bookService;

    @GetMapping("/{name}/books")
    public List<Book> getAllPublisherBooks(@PathVariable String name) {
        return bookService.getBooksByPublisher(name);
    }
}