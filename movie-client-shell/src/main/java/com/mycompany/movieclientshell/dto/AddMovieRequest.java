package com.mycompany.movieclientshell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMovieRequest {

    private String imdb;
    private String title;

}
