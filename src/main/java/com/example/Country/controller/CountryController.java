package com.example.Country.controller;

import com.example.Country.dto.MessageDTO;
import com.example.Country.model.CountryInfo;
import com.example.Country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class CountryController {
    @Autowired
    CountryService countryService;

    @GetMapping("/getAllCountyInfo")
    public List<CountryInfo> getAllCountryInfo() {
        return countryService.getAllCountryInfo();
    }

    @GetMapping("/process")
    public MessageDTO process(@RequestParam(value = "zipname") String zipname) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            countryService.unzip(zipname);
            countryService.listFileInFolder();
            messageDTO.setMessage("Processed");
            return messageDTO;
        } catch (Exception e) {
            messageDTO.setMessage(e.getMessage());
            return messageDTO;
        }
    }

    @GetMapping("/deleteAll")
    public String deleteAll() {
        try {
            countryService.deleteAll();
            return "All entries have been deleted";
        } catch (Exception e) {
            return "Error " + e.getMessage();
        }
    }

}
