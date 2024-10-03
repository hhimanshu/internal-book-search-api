package com.h2.repository;

import com.h2.entity.Book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT * FROM books WHERE search_vector @@ to_tsquery(:searchTerm)", nativeQuery = true)
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);

    List<Book> getBooksByPublisher(String publisher);
}