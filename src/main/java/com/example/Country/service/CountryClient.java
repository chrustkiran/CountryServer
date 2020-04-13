package com.example.Country.service;

import reactor.core.publisher.Mono;

public interface CountryClient {
    Mono<String> getCountryName(String countryCode);
}
