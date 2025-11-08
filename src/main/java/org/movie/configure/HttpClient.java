package org.movie.configure;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.ConnectionPool;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class HttpClient {
    @ConfigProperty(name = "okhttp.connect.timeout", defaultValue = "10")
    int connectTimeout;

    @ConfigProperty(name = "okhttp.read.timeout", defaultValue = "30")
    int readTimeout;

    @ConfigProperty(name = "okhttp.write.timeout", defaultValue = "30")
    int writeTimeout;

    @ConfigProperty(name = "okhttp.max.idle.connections", defaultValue = "5")
    int maxIdleConnections;

    @ConfigProperty(name = "okhttp.keep.alive.duration", defaultValue = "5")
    int keepAliveDuration;

    @Produces
    @Singleton
    @DefaultBean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(
                        maxIdleConnections,
                        keepAliveDuration,
                        TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .followRedirects(true);

        // HTTPS is supported by default with secure settings
        // Uncomment below only if you need custom SSL configuration

        // For self-signed certificates in development (NOT for production):
        // builder.hostnameVerifier((hostname, session) -> true);

        // For custom trust store:
        // builder.sslSocketFactory(customSSLSocketFactory, customTrustManager);

        return builder.build();
    }
}
