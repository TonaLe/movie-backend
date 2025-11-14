package org.movie.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.movie.service.MovieService;
import org.movie.service.integration.response.MovieResponse;

@Path("/api")
public class MovieResource {

    @Inject
    MovieService movieService;

    @GET
    @Path("/movies")
    @Produces(MediaType.APPLICATION_JSON)  // Add this!
    public MovieResponse getMovies(@QueryParam("query") String query, @QueryParam("sort_by") String sortBy) {
        if (StringUtils.isBlank(query)) {
            return movieService.fetchAllMovies();
        } else {
            return movieService.fetchMoviesByKeyword(query.trim());
        }
    }
}
