package com.example.Country.repositories;

import com.example.Country.model.CountryInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CountryInfoRepository extends MongoRepository<CountryInfo, Integer> {
}
