package org.movie.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.movie.entity.SearchMetrics;

import java.util.List;

@ApplicationScoped
public class SearchMetricsRepository implements PanacheRepository<SearchMetrics> {

    // Just returns Uni - no business logic
    public Uni<Void> incrementSearchCount(String searchTerm, Integer movieId,
                                          String title, String posterUrl) {
        return find("searchTerm = ?1 and movieId = ?2", searchTerm, movieId)
                .firstResult()
                .onItem().transformToUni(existing -> {
                    // Simple upsert logic
                    if (existing != null) {
                        existing.setCount(existing.getCount() + 1);
                        return Panache.withTransaction(() -> persist(existing));
                    } else {
                        // Create new
                        return Panache.withTransaction(() -> persist(SearchMetrics.builder()
                                .searchTerm(searchTerm)
                                .count(1)
                                .posterUrl(posterUrl)
                                .title(title)
                                .build()));
                    }
                })
                .replaceWithVoid();
    }

    /**
     * Find top trending movies overall
     */
    public Uni<List<SearchMetrics>> findTopTrending(int limit) {
        return find("ORDER BY count DESC")
                .page(0, limit)
                .list();
    }

    /**
     * Find top trending by search term
     */
    public Uni<List<SearchMetrics>> findTopTrendingBySearchTerm(String searchTerm, int limit) {
        return find("searchTerm = ?1 ORDER BY count DESC", searchTerm.toLowerCase().trim())
                .page(0, limit)
                .list();
    }

    /**
     * Get total count for movie (number of different search terms)
     */
    public Uni<Long> getTotalCountForMovie(Integer movieId) {
        return find("movieId", movieId).count();
    }

    /**
     * Get aggregated search count for a movie (sum of all counts)
     */
    public Uni<Long> getAggregatedCountForMovie(Integer movieId) {
        return find("SELECT COALESCE(SUM(m.count), 0) FROM SearchMetrics m WHERE m.movieId = ?1", movieId)
                .project(Long.class)
                .firstResult();
    }

    /**
     * Find by search term and movie ID
     */
    public Uni<SearchMetrics> findBySearchTermAndMovieId(String searchTerm, Integer movieId) {
        return find("searchTerm = ?1 and movieId = ?2",
                searchTerm.toLowerCase().trim(), movieId)
                .firstResult();
    }

    /**
     * Save a metric
     */
    public Uni<Void> saveMetric(SearchMetrics metric) {
        return Panache.withTransaction(() ->
                persistAndFlush(metric)
        ).replaceWithVoid();
    }

    /**
     * Find all metrics for a movie
     */
    public Uni<List<SearchMetrics>> findAllByMovieId(Integer movieId) {
        return find("movieId", movieId).list();
    }

    /**
     * Delete metrics by search term
     */
    public Uni<Long> deleteBySearchTerm(String searchTerm) {
        return Panache.withTransaction(() ->
                delete("searchTerm", searchTerm.toLowerCase().trim())
        );
    }

}
