package com.example.Country;

import com.example.Country.model.CountryInfo;
import com.example.Country.service.CountryClient;
import com.example.Country.service.CountryClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CountryClientTest {

    @Autowired
    CountryClient countryClient;

    @Test
    public void countrySuccessTest() throws JsonProcessingException {
        String countryName = countryClient.getCountryName("LK").block().get("name").toString();
        assertThat(countryName).isEqualTo("Sri Lanka");
    }

    @Test
    public void countryFailureTest() throws JsonProcessingException {
        try {
            String countryName = countryClient.getCountryName("ZZ").block().get("name").toString();
        } catch (Exception e){
            assertThat(e).isInstanceOf(WebClientResponseException.class);
        }
    }
}
