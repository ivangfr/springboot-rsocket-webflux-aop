package com.ivanfranchin.movieserver.mapper;

import com.ivanfranchin.movieserver.controller.dto.AddMovieRequest;
import com.ivanfranchin.movieserver.model.Movie;

public interface MovieMapper {

    Movie toMovie(AddMovieRequest addMovieRequest);
}
