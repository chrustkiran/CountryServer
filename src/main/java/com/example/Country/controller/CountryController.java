package com.example.Country.controller;

import com.example.Country.model.CountryInfo;
import com.example.Country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.nio.file.*;

@CrossOrigin
@RestController
public class CountryController {
    @Autowired
    CountryService countryService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<CountryInfo> process(@RequestPart("file") FilePart filePart) throws IOException, InterruptedException {
        if(!filePart.filename().split("\\.")[1].equals("zip")){
            return Flux.error(new Throwable("Please upload a zip"));
        }
        return countryService.process(filePart).onErrorResume(
                throwable -> Flux.error(new Throwable())
        );
    }

    @GetMapping("/deleteAll")
    public ResponseEntity deleteAll() {
        try {
            countryService.deleteAll();
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        }
    }

}
