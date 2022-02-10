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
public class VolumeInfo implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 55540069981022979L;
    protected String title;
    protected String[] authors;
}
