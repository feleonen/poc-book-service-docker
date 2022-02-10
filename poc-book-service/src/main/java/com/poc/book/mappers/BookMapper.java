package com.poc.book.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poc.book.domain.Book;
import com.poc.book.dto.BookDTO;


@Mapper(imports = java.util.Arrays.class)
public interface BookMapper {

    Book dtoToBook(BookDTO dto);
    
    BookDTO bookToDTO(Book book);
    
    @Mapping(source = "volumeInfo.title", target = "title")
    @Mapping(target = "author", expression="java(Arrays.toString(book.getVolumeInfo().getAuthors()))")
    BookDTO bookToDTO(com.poc.book.model.gb.Book book);
}
