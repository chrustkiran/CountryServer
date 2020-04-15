package com.example.Country.service;

import com.example.Country.model.CountryInfo;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface CountryService {
     Flux<CountryInfo> process(FilePart filePart) throws IOException, InterruptedException;
     void deleteAll();
     Flux<CountryInfo> getAllCountryInfo();
}
