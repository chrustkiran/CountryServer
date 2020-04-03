package com.example.Country.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CountryAPI {

    @Value("${api.url}")
    private String countryAPIURL;

    public String getCountryName(String countryCode) throws JsonProcessingException {

        final String uri = countryAPIURL + countryCode;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(result, Map.class);
            if (map.containsKey("name")) {
                return map.get("name").toString();
            }
            return "ERROR";
        } catch (Exception e) {
            return "ERROR";
        }
    }
}
