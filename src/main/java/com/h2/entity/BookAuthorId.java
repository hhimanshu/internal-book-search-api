package com.h2.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class BookAuthorId implements Serializable {

    private Long bookId;
    private Long authorId;

    public Long getBookId() {
        return bookId;
    }

    public Long getAuthorId() {
        return authorId;
    }

}