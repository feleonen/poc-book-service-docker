package com.poc.book.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.book.dto.BookDTO;
import com.poc.book.service.BookService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class BookAPIController {

    private final BookService bookService;

    @GetMapping("book/find")
    public ResponseEntity<List<BookDTO>> getFolderDetailsByFolderId(@RequestParam(value = "q", required = true) String q)
            throws Exception {
        return new ResponseEntity<List<BookDTO>>(bookService.findBooks(q), HttpStatus.OK);
    }

}
