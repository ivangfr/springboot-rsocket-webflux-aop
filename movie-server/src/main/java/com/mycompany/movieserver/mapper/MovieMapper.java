package com.mycompany.movieserver.mapper;

import com.mycompany.movieserver.controller.dto.AddMovieRequest;
import com.mycompany.movieserver.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Movie toMovie(AddMovieRequest addMovieRequest);
}
