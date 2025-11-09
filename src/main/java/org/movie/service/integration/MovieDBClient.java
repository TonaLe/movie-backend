package org.movie.service.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.movie.configure.HttpClient;
import org.movie.service.integration.response.MovieResponse;

import java.io.DataInput;
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

    public MovieResponse fetchMovie() {
        log.debug("Fetching movie data...");

        try (Response response = httpClient.okHttpClient()
                .newCall(buildRequest())
                .execute()) {

            validateResponse(response);
            return objectMapper.readValue(response.body().string(), MovieResponse.class);

        } catch (IOException e) {
            log.error("Failed to fetch movie: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to fetch movie data", e);
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

    private Request buildRequest() {

        return new Request.Builder()
                .url(host + discoverPath)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer " + accessToken)
                .build();
    }
}
