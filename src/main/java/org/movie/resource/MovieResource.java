package org.movie.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api")
public class MovieResource {

    @GET
    @Path("/movies")
    public String getMovies() {
        return "Movies";
    }
}
