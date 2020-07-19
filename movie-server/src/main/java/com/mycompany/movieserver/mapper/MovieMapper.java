package com.mycompany.movieserver.mapper;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.model.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(AddMovieRequest addMovieRequest);

}
