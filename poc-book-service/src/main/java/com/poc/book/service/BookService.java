package com.poc.book.service;

import java.util.List;

import com.poc.book.dto.BookDTO;

public interface BookService {
    List<BookDTO> findBooks(String q);
}
