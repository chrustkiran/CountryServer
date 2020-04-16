package com.example.Country.service;

import com.example.Country.dto.Constants;
import com.example.Country.model.CountryInfo;
import com.example.Country.repositories.CountryInfoRepository;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
public class CountryServiceImpl implements CountryService {

    Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);

    @Autowired
    CountryClient countryClient;

    @Autowired
    FileUploader fileUploader;

    @Autowired
    Unzipper unzipper;

    @Autowired
    CountryInfoRepository countryInfoRepository;

    @Value("${extract.destination}")
    private String destination;

    @Override
    public Flux<CountryInfo> process(FilePart filePart) throws IOException, InterruptedException {

        return fileUploader.upload(filePart).flatMapMany(
                fileName -> {
                    try {
                        unzipper.unzip(fileName);
                        return processFile(fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return Flux.error(new Throwable(Constants.FILE_NOT_FOUND));
                    } catch (ZipException e) {
                        e.printStackTrace();
                        return Flux.error(new Throwable(Constants.UNZIPPING_ERROR));
                    }
                }
        );
    }

    private void save(CountryInfo countryInfo) {
        countryInfoRepository.save(countryInfo).subscribe();
    }


    @Override
    public Flux<CountryInfo> getAllCountryInfo() {
        return countryInfoRepository.findAll().doAfterTerminate(
                () -> {
                    this.deleteAll();
                }
        );
    }

    private Flux<CountryInfo> getAllCountryInfoByname(String filename) {
        logger.info("fetching...");
        return countryInfoRepository.findByZipFileName(filename);
    }

    @Override
    public void deleteAll() {
        countryInfoRepository.deleteAll().subscribe();
    }


    private CountryInfo composeCountryInfo(String zipFilename, String extractedFileName, String fileContent, String countryName, String status) {
        CountryInfo countryInfo = new CountryInfo();
        countryInfo.setZipFileName(zipFilename);
        countryInfo.setExtractedFileName(extractedFileName);
        countryInfo.setFileContent(fileContent);
        countryInfo.setCountryName(countryName);
        countryInfo.setStatus(status);
        countryInfo.setDate(new Date());
        return countryInfo;
    }

    private Flux<CountryInfo> processFile(String zipFilename) throws FileNotFoundException {
        String folderName = zipFilename.split("\\.")[0];
        File extractedFolder = new File(destination + folderName);
        List<File> files = Arrays.asList(extractedFolder.listFiles());
        return Flux.fromIterable(files).flatMap(file -> {
            ArrayList lines;
            try {
                lines = convertFileToList(file);
                if (lines.size() == 0) {
                    logger.info("no content in " + file.getName());
                    CountryInfo countryInfo = composeCountryInfo(zipFilename, file.getName(), " ", " ", Constants.NO_CONTENT);
                    save(countryInfo);
                    return Flux.just(countryInfo);
                }
                return Flux.fromIterable(lines).map(line -> {
                    try {
                        String countryName = countryClient.getCountryName(line.toString()).block();
                        CountryInfo countryInfo = composeCountryInfo(zipFilename, file.getName(), line.toString(), countryName, Constants.SUCCESS);
                        save(countryInfo);
                        return countryInfo;
                    } catch (WebClientResponseException e) {
                        CountryInfo countryInfo = composeCountryInfo(zipFilename, file.getName(), line.toString(), " ", Constants.NO_SUCH_COUNTRY);
                        save(countryInfo);
                        return countryInfo;
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                CountryInfo countryInfo = composeCountryInfo(zipFilename, file.getName(), "", "", Constants.ERROR);
                save(countryInfo);
                return Flux.error(new Throwable(Constants.ERROR));
            }
        });

    }

    private ArrayList<String> convertFileToList(File file) throws FileNotFoundException {
        logger.info("reading " + file.getName());
        Scanner scanner;
        ArrayList lines = new ArrayList();
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException fileNotFountException) {
            fileNotFountException.printStackTrace();
            throw fileNotFountException;
        }
        return lines;
    }
}
