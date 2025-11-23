package org.movie.service.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.movie.dto.SearchMetricsEvent;
import org.movie.service.integration.MovieDBClient;
import org.movie.service.integration.response.MovieResponse;

@ApplicationScoped
public class MovieService {

    @Inject
    MovieDBClient movieDBClient;

    @Inject
    MetricsService metricsService;

    public MovieResponse fetchAllMovies() {
        return movieDBClient.fetchAllMovies();
    }

    public MovieResponse fetchMoviesByKeyword(String keyword) {
        //        Using Uni
//        return Uni.createFrom().item(() -> movieDBClient.fetchMoviesByKeyword(keyword))
//                .onItem().invoke(response -> {
//                    // This fires concurrently with returning response
//                    if (StringUtils.isNotBlank(keyword) && response != null) {
//                        metricsService.recordSearchMetrics(keyword, response.getResults());
//                    }
//                });

        var movieResponse = movieDBClient.fetchMoviesByKeyword(keyword);
        // Fire async event - completely non-blocking, returns immediately
        if (StringUtils.isNotBlank(keyword) && movieResponse != null && movieResponse.getResults() != null) {
            metricsService.recordSearchMetrics(keyword, movieResponse.getResults());
        }

        return movieResponse;
    }
}
