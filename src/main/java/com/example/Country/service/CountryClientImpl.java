package com.example.Country.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class CountryClientImpl implements CountryClient {

    @Value("${api.url}")
    private String countryAPIURL;

    private WebClient client = WebClient.create();

    @Override
    public Mono<TypedMap> getCountryName(String countryCode) {
        return client.get()
                .uri(countryAPIURL + countryCode)
                .retrieve()
                .bodyToMono(TypedMap.class);
    }

    public static class TypedMap extends HashMap<String, Object> {
    }
}

