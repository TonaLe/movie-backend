package org.movie.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metrics", indexes = {
        @Index(name = "idx_search_term", columnList = "search_term"),
        @Index(name = "idx_count", columnList = "count DESC")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMetrics extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_term", length = 1000)
    private String searchTerm;

    @Column(name = "count")
    private Integer count;

    @Column(name = "poster_url", length = 1000)
    private String posterUrl;

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "title", length = 1000)
    private String title;
}
