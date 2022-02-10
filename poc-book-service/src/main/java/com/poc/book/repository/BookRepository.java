package com.poc.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.poc.book.domain.Book;

public interface BookRepository extends JpaRepository<Book, String>{

    static final String FIND_BY_AUTHOR_OR_TITLE = "SELECT b.id, b.author, b.title "
            + " FROM dbo.BOOK b WHERE b.author LIKE %?1% OR b.title LIKE %?1%";
    @Modifying
    @Query(value = FIND_BY_AUTHOR_OR_TITLE, nativeQuery = true)
    List<Book> findByAuthorOrTitle(String q);
    
}
