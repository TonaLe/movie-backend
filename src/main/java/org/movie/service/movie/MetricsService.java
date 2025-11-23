package org.movie.service.movie;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.movie.entity.SearchMetrics;
import org.movie.repository.SearchMetricsRepository;
import org.movie.service.integration.response.MovieResult;

import java.util.List;

@Slf4j
@ApplicationScoped
public class MetricsService {

    @Inject
    SearchMetricsRepository metricsRepository;


    /**
     * Record search metrics - Fire and forget
     * Service layer converts MovieResult list to reactive pipeline
     */
    public void recordSearchMetrics(String searchTerm, List<MovieResult> movies) {
        if (movies == null || movies.isEmpty()) {
            return;
        }

        log.debug("Recording metrics for {} movies", movies.size());

        // Convert list to reactive Multi and process
        Multi.createFrom().iterable(movies)
                .onItem().transformToUniAndConcatenate(movie -> {
                    // Convert MovieResult to SearchMetrics entity
                    String posterUrl = movie.getPosterPath() != null
                            ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath()
                            : null;

                    // Call repository method - it returns Uni
                    return metricsRepository.incrementSearchCount(
                            searchTerm,
                            movie.getId(),
                            movie.getTitle(),
                            posterUrl
                    );
                })
                .collect().asList()
                .subscribe().with(
                        success -> log.debug("Successfully recorded metrics for: {}", searchTerm),
                        failure -> log.error("Failed to record metrics: {}", failure.getMessage())
                );

        // Method returns IMMEDIATELY - work happens in background
    }

    /**
     * Alternative: Return Uni if you want control over subscription
     */
    public Uni<Void> recordSearchMetricsReactive(String searchTerm, List<MovieResult> movies) {
        if (movies == null || movies.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        return Multi.createFrom().iterable(movies)
                .onItem().transformToUniAndConcatenate(movie -> {
                    String posterUrl = movie.getPosterPath() != null
                            ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath()
                            : null;

                    return metricsRepository.incrementSearchCount(
                            searchTerm,
                            movie.getId(),
                            movie.getTitle(),
                            posterUrl
                    );
                })
                .collect().asList()
                .replaceWithVoid()
                .onFailure().invoke(err ->
                        log.error("Failed to record metrics: {}", err.getMessage())
                )
                .onFailure().recoverWithNull();
    }

    /**
     * Get top trending - Just delegate to repository
     */
    public Uni<List<SearchMetrics>> getTopTrending(int limit) {
        return metricsRepository.findTopTrending(limit);
    }

    /**
     * Get trending by search term - Just delegate to repository
     */
    public Uni<List<SearchMetrics>> getTopTrendingBySearchTerm(String searchTerm, int limit) {
        return metricsRepository.findTopTrendingBySearchTerm(searchTerm, limit);
    }

    /**
     * Get total count for movie - Just delegate to repository
     */
    public Uni<Long> getTotalCountForMovie(Integer movieId) {
        return metricsRepository.getTotalCountForMovie(movieId);
    }
}



