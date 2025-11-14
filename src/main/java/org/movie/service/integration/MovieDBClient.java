package org.movie.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.movie.configure.HttpClient;
import org.movie.constant.MovieAPIConstant;
import org.movie.service.integration.response.MovieResponse;

import java.io.IOException;

@Slf4j
@ApplicationScoped
public class MovieDBClient {
    @Inject
    HttpClient httpClient;

    @ConfigProperty(name = "db.client.access_token")
    String accessToken;

    @ConfigProperty(name = "db.client.api_key")
    String apiKey;

    @ConfigProperty(name = "db.client.host")
    String host;

    @ConfigProperty(name = "db.client.discover_path")
    String discoverPath;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Fetch all movies without keyword filter
     */
    public MovieResponse fetchAllMovies() {
        log.debug("Fetching all movies...");
        return fetchMovies(null);
    }

    /**
     * Fetch movies by keyword
     * @param keyword search keyword
     */
    public MovieResponse fetchMoviesByKeyword(String keyword) {
        log.debug("Fetching movies with keyword: {}", keyword);
        return fetchMovies(keyword);
    }


    /**
     * Internal method to fetch movies with optional keyword
     * @param keyword optional search keyword (can be null)
     */
    private MovieResponse fetchMovies(String keyword) {
        try (Response response = httpClient.okHttpClient()
                .newCall(buildMovieRequest(keyword))
                .execute()) {

            validateResponse(response);
            return objectMapper.readValue(response.body().string(), MovieResponse.class);

        } catch (IOException e) {
            String errorMsg = StringUtils.isBlank(keyword)
                    ? "Failed to fetch all movies"
                    : "Failed to fetch movies with keyword '" + keyword + "'";
            log.error("{}: {}", errorMsg, e.getMessage(), e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    private void validateResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response.code());
        }

        if (response.body() == null) {
            throw new IOException("Response body is empty");
        }
    }

    /**
     * Build request for fetching movies with optional keyword filter
     * @param keyword optional search keyword (can be null or empty)
     */
    private Request buildMovieRequest(String keyword) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(host + discoverPath).newBuilder()
                .addQueryParameter(MovieAPIConstant.PARAM_INCLUDE_ADULT,
                        MovieAPIConstant.VALUE_INCLUDE_ADULT)
                .addQueryParameter(MovieAPIConstant.PARAM_INCLUDE_VIDEO,
                        MovieAPIConstant.VALUE_INCLUDE_VIDEO)
                .addQueryParameter(MovieAPIConstant.PARAM_LANGUAGE,
                        MovieAPIConstant.VALUE_LANGUAGE)
                .addQueryParameter(MovieAPIConstant.PARAM_PAGE,
                        MovieAPIConstant.VALUE_PAGE_DEFAULT)
                .addQueryParameter(MovieAPIConstant.PARAM_SORT_BY,
                        MovieAPIConstant.VALUE_SORT_BY_POPULARITY);

        // Add keyword parameter only if provided
        if (StringUtils.isNotBlank(keyword)) {
            urlBuilder.addQueryParameter(MovieAPIConstant.PARAM_WITH_KEYWORDS, keyword);
        }

        HttpUrl url = urlBuilder.build();

        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer " + accessToken)
                .build();
    }
}
