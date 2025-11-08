package org.movie.service.dto.integration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.movie.configure.HttpClient;

@ApplicationScoped
public class MovieDBClient {
    @Inject
    HttpClient httpClient;

    public
}
