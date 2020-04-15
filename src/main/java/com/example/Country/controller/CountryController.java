package com.example.Country.controller;

import com.example.Country.dto.Constants;
import com.example.Country.model.CountryInfo;
import com.example.Country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;

@CrossOrigin
@RestController
public class CountryController {
    @Autowired
    CountryService countryService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountryInfo> process(@RequestPart("file") FilePart filePart) throws IOException, InterruptedException {
        if (!filePart.filename().split("\\.")[1].equals("zip")) {
            return Flux.error(new Throwable(Constants.UPLOAD_ZIP));
        }
        return countryService.process(filePart).onErrorResume(
                throwable -> Flux.error(throwable)
       );
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> testProcess() throws MalformedURLException {
        return Flux.range(0, 10).delayElements(Duration.ofSeconds(1));
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

    @GetMapping(value = "/findAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountryInfo> findAll() {
       return countryService.getAllCountryInfo().delayElements(Duration.ofSeconds(1));
    }

}
