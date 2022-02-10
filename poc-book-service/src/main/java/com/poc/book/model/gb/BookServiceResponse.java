package com.poc.book.model.gb;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookServiceResponse implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4388896253635569377L;
    protected String kind;
    private int totalItems;
    protected List<Book> items;
}
