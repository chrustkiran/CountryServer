package com.example.Country;

import com.example.Country.controller.CountryController;
import com.example.Country.model.CountryInfo;
import com.example.Country.repositories.CountryInfoRepository;
import com.example.Country.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;


import java.net.MalformedURLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {

    @Autowired
    CountryInfoRepository countryInfoRepository;

    @Autowired
    private WebTestClient webClient;


    @Test
    void processTest() throws MalformedURLException {

        final UrlResource resource = new UrlResource("file:example.zip");
        MultiValueMap<String, UrlResource> data = new LinkedMultiValueMap<>();
        data.add("file", resource);


        webClient.post()
                .uri("/process")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(data))
                .exchange().expectStatus().isOk()
                .expectBodyList(CountryInfo.class).hasSize(9);
    }

    @Test
    void findAllTest() {
        webClient.get()
                .uri("/findAll")
                .exchange().expectStatus().isOk();
    }
}
