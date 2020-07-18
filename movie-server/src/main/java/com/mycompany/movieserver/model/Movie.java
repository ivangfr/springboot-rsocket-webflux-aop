package com.mycompany.movieserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "movies")
public class Movie {

    @Id
    private String imdb;
    private String title;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
