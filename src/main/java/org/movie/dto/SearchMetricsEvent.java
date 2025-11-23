package org.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movie.service.integration.response.MovieResult;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchMetricsEvent {
    private String searchTerm;
    private List<MovieResult> movies;
}