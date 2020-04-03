package com.example.Country.service;

import com.example.Country.api.CountryAPI;
import com.example.Country.model.CountryInfo;
import com.example.Country.repositories.CountryInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
public class CountryService {

    @Autowired
    CountryAPI countryAPI;

    @Autowired
    CountryInfoRepository countryInfoRepository;

    @Value("${extract.destination}")
    private String destination;
    private String zipFilename;

    public void unzip(String source) throws ZipException {
        try {
            this.zipFilename = source.split("\\.")[0];
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination + this.zipFilename);
        } catch (ZipException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public void listFileInFolder() {
        try {
            File extractedFolder = new File(destination + this.zipFilename);
            System.out.println(extractedFolder.listFiles().length);
            for (final File textFile : extractedFolder.listFiles()) {
                System.out.println("Reading " + textFile.getName());
                readFile(textFile);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    private void readFile(File file) {
        try {
            boolean noContent = true;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                noContent = false;
                String line = scanner.nextLine();
                String countryName = countryAPI.getCountryName(line);
                if (!countryName.equals("ERROR")) {
                    saveToDb(zipFilename, file.getName(), line, countryName, "SUCCESS");
                } else {
                    saveToDb(zipFilename, file.getName(), line, "", "NO_SUCH_COUNTRY");
                }
            }
            if (noContent) {
                saveToDb(zipFilename, file.getName(), "", "", "NO_CONTENT");
            }
        } catch (Exception e) {
            saveToDb(zipFilename, file.getName(), "", "", "ERROR");
        }
    }

    private void saveToDb(String zipFilename, String extractedFileName, String fileContent, String countryName, String status) {
        // System.out.println("Saving to database");
        CountryInfo countryInfo = new CountryInfo();
        countryInfo.setZipFileName(zipFilename);
        countryInfo.setExtractedFileName(extractedFileName);
        countryInfo.setFileContent(fileContent);
        countryInfo.setCountryName(countryName);
        countryInfo.setStatus(status);
        countryInfo.setDate(new Date());
        countryInfoRepository.save(countryInfo);
        // System.out.println("Saving successfull");
    }

    public List<CountryInfo> getAllCountryInfo() {
        return countryInfoRepository.findAll();
    }

    public void deleteAll() {
        countryInfoRepository.deleteAll();
    }
}
