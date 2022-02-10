package com.poc.book.model.gb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5260861729255789592L;
    protected String id;
    protected String etag;
    protected String selfLink;
    protected VolumeInfo volumeInfo;
}
