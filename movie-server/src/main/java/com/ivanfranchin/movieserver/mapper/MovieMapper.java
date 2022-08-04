package com.ivanfranchin.movieserver.mapper;

import com.ivanfranchin.movieserver.controller.dto.AddMovieRequest;
import com.ivanfranchin.movieserver.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Movie toMovie(AddMovieRequest addMovieRequest);
}
