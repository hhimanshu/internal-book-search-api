package com.h2.repository;

import com.h2.entity.BookAuthor;
import com.h2.entity.BookAuthorId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {

    List<BookAuthor> findByAuthorAuthorId(Long authorId);
}