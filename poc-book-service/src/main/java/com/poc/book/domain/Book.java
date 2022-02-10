package com.poc.book.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "BOOK", schema = "dbo")
public class Book {

    public Book(String id, String author, String title) {
        super();
        this.id = id;
        this.author = author;
        this.title = title;
    }
    @Id
    private String id;
    private String author;
    private String title;
}