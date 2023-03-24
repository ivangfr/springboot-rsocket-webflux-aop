package com.ivanfranchin.movieserver.mapper;

import com.ivanfranchin.movieserver.controller.dto.AddMovieRequest;
import com.ivanfranchin.movieserver.model.Movie;
import org.springframework.stereotype.Service;

@Service
public class MovieMapperImpl implements MovieMapper {

    @Override
    public Movie toMovie(AddMovieRequest addMovieRequest) {
        if (addMovieRequest == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setImdb(addMovieRequest.imdb());
        movie.setTitle(addMovieRequest.title());
        return movie;
    }
}
