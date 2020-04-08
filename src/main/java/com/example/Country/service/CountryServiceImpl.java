package com.example.Country.service;

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
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Scanner;

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

    @Value("${delay}")
    private long delay;

    @Override
    public Flux<CountryInfo> process(FilePart filePart) throws IOException, InterruptedException {
        fileUploader.upload(filePart).doOnComplete(() -> {
            logger.info("Finished uploading " + filePart.filename());
            try {
                unzipper.unzip(filePart.filename());
                processFile(filePart.filename());
            } catch (ZipException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }).doOnError(error -> logger.error("Error uploading file ", error)).subscribe();
        return getAllCountryInfoByname(filePart.filename()).delaySubscription(Duration.ofMillis(delay));
    }

    private void save(CountryInfo countryInfo) {
        countryInfoRepository.save(countryInfo).subscribe();
    }


    private Flux<CountryInfo> getAllCountryInfo() {
        return countryInfoRepository.findAll();
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

    private void processFile(String zipFilename) throws FileNotFoundException {
        try {
            String folderName = zipFilename.split("\\.")[0];
            File extractedFolder = new File(destination + folderName);
            for (final File textFile : extractedFolder.listFiles()) {
                logger.info("Started processing " + textFile.getName());
                boolean noContent = true;
                Scanner scanner = new Scanner(textFile);
                while (scanner.hasNextLine()) {
                    noContent = false;
                    String line = scanner.nextLine();
                    countryClient.getCountryName(line).doOnSuccess(
                            res -> {
                                CountryInfo countryInfo = composeCountryInfo(zipFilename, textFile.getName(), line, res.get("name").toString(), "SUCCESS");
                                save(countryInfo);
                            }
                    ).doOnError(
                            throwable -> {
                                if (throwable instanceof WebClientResponseException) {
                                    CountryInfo countryInfo = composeCountryInfo(zipFilename, textFile.getName(), line, "", "NO_SUCH_COUNTRY");
                                    save(countryInfo);
                                } else {
                                    CountryInfo countryInfo = composeCountryInfo(zipFilename, textFile.getName(), line, "", "ERROR");
                                    save(countryInfo);
                                }
                            }
                    ).subscribe();
                }
                if (noContent) {
                    logger.info(textFile.getName() + " has no content");
                    CountryInfo countryInfo = composeCountryInfo(zipFilename, textFile.getName(), "", "", "NO_CONTENT");
                    save(countryInfo);
                }
            }

        } catch (
                FileNotFoundException e) {
            throw e;
        }

    }
}
