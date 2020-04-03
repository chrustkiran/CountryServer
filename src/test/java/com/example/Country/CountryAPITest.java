package com.example.Country;

import com.example.Country.api.CountryAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CountryAPITest {

    @Autowired
    CountryAPI countryAPI;

    @Test
    public void countryTest() throws JsonProcessingException {
        System.out.println(countryAPI.getCountryName("lk"));
        System.out.println(countryAPI.getCountryName("LK"));
        System.out.println(countryAPI.getCountryName("uzk"));
    }
}
