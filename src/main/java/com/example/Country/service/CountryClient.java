package com.example.Country.service;

import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public interface CountryClient {
    Mono<CountryClientImpl.TypedMap> getCountryName(String countryCode);
}
