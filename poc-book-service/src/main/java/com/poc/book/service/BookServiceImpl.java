package com.poc.book.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.book.dto.BookDTO;
import com.poc.book.mappers.BookMapper;
import com.poc.book.model.gb.BookServiceResponse;
import com.poc.book.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    
    private final BookMapper bookMapper;
    
    private final JmsTemplate jmsTemplate;
    
    private void publishBooks(List<BookDTO> lstBooksDTO) {
        ObjectMapper mapper = new ObjectMapper();
        lstBooksDTO.forEach(aBook -> {
            try {
                jmsTemplate.convertAndSend("poc.book", mapper.writeValueAsString(aBook));
            } catch (JmsException | JsonProcessingException ex) {
                log.error("Error posting to queue, exception is ", ex);
            }
        });
    }
    
    private List<BookDTO> getAPIBooksByTitle(String q){
        List<BookDTO> lstBooksDTO = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<BookServiceResponse> entity = restTemplate.getForEntity("https://www.googleapis.com/books/v1/volumes?q=+intitle:"+q, BookServiceResponse.class);
        BookServiceResponse response = entity.getBody();
        log.debug("Books {}", response.getItems());
        if(null != response && null != response.getItems() && !response.getItems().isEmpty())
            lstBooksDTO = response.getItems().stream().map(bookMapper::bookToDTO).collect(Collectors.toList());
        return lstBooksDTO;
    }
    
    private List<BookDTO> getAPIBooksByAutor(String q){
        List<BookDTO> lstBooksDTO = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<BookServiceResponse> entity = restTemplate.getForEntity("https://www.googleapis.com/books/v1/volumes?q=+inauthor:"+q, BookServiceResponse.class);
        BookServiceResponse response = entity.getBody();
        log.debug("Books {}", response.getItems());
        if(null != response && null != response.getItems() && !response.getItems().isEmpty())
            lstBooksDTO = response.getItems().stream().map(bookMapper::bookToDTO).collect(Collectors.toList());
        return lstBooksDTO;
    }
    
    private List<BookDTO> getBooksFromAPI(String q){
        List<BookDTO> lstBooksDTO = null;
        List<BookDTO> lstBooksByTitle = null;
        List<BookDTO> lstBooksByAutor = null;
        lstBooksByTitle = getAPIBooksByTitle(q);
        lstBooksByAutor = getAPIBooksByAutor(q);
        lstBooksDTO = Stream.of(lstBooksByTitle, lstBooksByAutor).flatMap(List::stream).collect(Collectors.toList());
        publishBooks(lstBooksDTO.stream().distinct().collect(Collectors.toList()));
        return lstBooksDTO.stream().distinct().collect(Collectors.toList());
    }
    
    @Override
    public List<BookDTO> findBooks(String q) {
        List<BookDTO> lstBooksDTO = null; 
        lstBooksDTO = bookRepository.findByAuthorOrTitle(q).stream().map(bookMapper::bookToDTO).collect(Collectors.toList());
        if(null == lstBooksDTO || (null != lstBooksDTO && lstBooksDTO.isEmpty()))
            lstBooksDTO = getBooksFromAPI(q);
        return lstBooksDTO;
    }
}
