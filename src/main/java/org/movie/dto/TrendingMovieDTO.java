package org.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingMovieDTO {
    private Integer movieId;
    private String title;
    private String posterUrl;
    private Integer searchCount;
    private String searchTerm;
}