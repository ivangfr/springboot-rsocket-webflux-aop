package com.mycompany.movieclientshell.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieDto {

    private String imdb;
    private String title;
    private LocalDateTime lastModifiedDate;
    private Integer likes;
    private Integer dislikes;

}
