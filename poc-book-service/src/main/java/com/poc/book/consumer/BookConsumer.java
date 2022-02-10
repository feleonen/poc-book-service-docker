package com.poc.book.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.book.dto.BookDTO;
import com.poc.book.mappers.BookMapper;
import com.poc.book.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BookConsumer {

    private final BookRepository bookRepository;
    
    private final BookMapper bookMapper;
    
    @JmsListener(destination = "poc.book")
    public void consumeMessage(String message) {
        log.debug("Message received from activemq queue {}" + message);
        BookDTO aBookDTO = null;
        try {
            aBookDTO = (BookDTO) new ObjectMapper().readValue(message, BookDTO.class);
            if(!bookRepository.findById(aBookDTO.getId()).isPresent())
                bookRepository.save(bookMapper.dtoToBook(aBookDTO));
        } catch (JsonProcessingException ex) {
            log.error("Error consuming message, exception is ", ex);
        }
        
    }

}
