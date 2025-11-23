package org.movie.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.movie.dto.TrendingMovieDTO;
import org.movie.entity.SearchMetrics;
import org.movie.service.movie.MetricsService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/trending")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrendingResource {

    @Inject
    MetricsService metricsService;

    /**
     * Get top trending movies across all searches
     *
     * Example: GET /api/trending?limit=10
     */
    @GET
    public Uni<List<TrendingMovieDTO>> getTopTrending(@QueryParam("limit") int limit) {
        return metricsService.getTopTrending(limit) // Returns Uni<List<SearchMetrics>>
                .onItem().transform(metrics ->  // Transform when data arrives
                        metrics.stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList())
                );
    }

    /**
     * Get top trending movies for a specific search term
     * Example: GET /api/trending/search?term=action&limit=10
     */
    @GET
    @Path("/search")
    public Uni<List<TrendingMovieDTO>> getTopTrendingBySearchTerm(
            @QueryParam("term") String searchTerm,
            @QueryParam("limit") @DefaultValue("20") int limit) {

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("Search term is required");
        }

        if (limit < 1 || limit > 100) {
            throw new BadRequestException("Limit must be between 1 and 100");
        }

        return metricsService.getTopTrendingBySearchTerm(
                searchTerm.trim(), limit)
                .onItem()
                .transform(item ->  // Transform when data arrives
                         item.stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList()));
    }

    /**
     * Get total search count for a specific movie
     *
     * Example: GET /api/trending/movie/12345
     */
    @GET
    @Path("/movie/{movieId}")
    public Uni<TrendingMovieDTO> getMovieSearchCount(@PathParam("movieId") Integer movieId) {
        // Service returns Uni<Long>
        return metricsService.getTotalCountForMovie(movieId)
                .onItem().transform(totalCount -> {
                    TrendingMovieDTO dto = new TrendingMovieDTO();
                    dto.setMovieId(movieId);
                    dto.setSearchCount(totalCount != null ? totalCount.intValue() : 0);
                    return dto;
                });
    }

    private TrendingMovieDTO toDTO(SearchMetrics metric) {
        TrendingMovieDTO dto = new TrendingMovieDTO();
        dto.setMovieId(metric.getMovieId());
        dto.setTitle(metric.getTitle());
        dto.setPosterUrl(metric.getPosterUrl());
        dto.setSearchCount(metric.getCount());
        dto.setSearchTerm(metric.getSearchTerm());
        return dto;
    }
}
