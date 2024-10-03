package com.h2.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookAuthorId implements Serializable {

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "author_id")
    private Long authorId;

    public BookAuthorId() {}

    public BookAuthorId(Long bookId, Long authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookAuthorId that = (BookAuthorId) o;

        return Objects.equals(bookId, that.bookId) &&
               Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, authorId);
    }
}