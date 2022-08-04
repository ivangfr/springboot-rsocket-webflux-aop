package com.ivanfranchin.movieclientshell;

import com.ivanfranchin.movieclientshell.dto.AddMovieRequest;
import com.ivanfranchin.movieclientshell.dto.MovieResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;

@NativeHint(
        types = @TypeHint(
                types = {AddMovieRequest.class, MovieResponse.class},
                access = {TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS}
        )
)
@SpringBootApplication
public class MovieClientShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieClientShellApplication.class, args);
    }
}
