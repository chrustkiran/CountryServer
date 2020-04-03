package com.example.Country;

import com.example.Country.service.CountryService;
import net.lingala.zip4j.exception.ZipException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class UnzipTest {

    @Autowired
    CountryService countryService;

    @Test
    void unzip() throws ZipException {
        System.out.println("Started unzipping");
        countryService.unzip("src/main/resources/example.zip");
        System.out.println("Stopped unzipping");
    }

    @Test
    void readFile() throws FileNotFoundException {
        countryService.listFileInFolder();
    }
}
