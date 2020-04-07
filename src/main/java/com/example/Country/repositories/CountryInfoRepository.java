package com.example.Country.repositories;

import com.example.Country.model.CountryInfo;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;


public interface CountryInfoRepository extends ReactiveMongoRepository<CountryInfo, Integer> {
    @Query("{ 'zipFileName': ?0 }")
    Flux<CountryInfo> findByZipFileName(String zipFileName);
}
