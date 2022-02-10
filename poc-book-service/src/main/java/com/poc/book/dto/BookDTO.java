package com.poc.book.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDTO {
    
    public BookDTO(String id, String author, String title) {
        super();
        this.id = id;
        this.author = author;
        this.title = title;
    }
    private String id;
    private String author;
    private String title;
}
