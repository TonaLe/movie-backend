package org.movie.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.movie.service.integration.MovieDBClient;
import org.movie.service.integration.response.MovieResponse;

@ApplicationScoped
public class MovieService {

    @Inject
    MovieDBClient movieDBClient;

    public MovieResponse fetchMovie() {
        return movieDBClient.fetchMovie();
    }
}
