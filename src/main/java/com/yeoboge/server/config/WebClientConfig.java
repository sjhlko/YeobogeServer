package com.yeoboge.server.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    private static final int MAX_MEMORY_SIZE = 2 * 1024 * 1024;

    @Value(value="${WEB_CLIENT_BASE_URL}")
    private String BASE_URL;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .uriBuilderFactory(uriBuilderFactory())
                .exchangeStrategies(exchangeStrategies())
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .build();
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("http-pool")
                .maxConnections(100)
                .pendingAcquireTimeout(Duration.ofMillis(0))
                .pendingAcquireMaxCount(-1)
                .maxIdleTime(Duration.ofMillis(1000L))
                .build();
    }

    private DefaultUriBuilderFactory uriBuilderFactory() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return uriBuilderFactory;
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000); // 10s
    }

    private ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE))
                .build();
    }
}
