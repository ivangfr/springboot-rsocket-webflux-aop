package com.ivanfranchin.movieserver.model;

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
    private Integer likes = 0;
    private Integer dislikes = 0;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
