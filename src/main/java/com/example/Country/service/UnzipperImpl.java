package com.example.Country.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UnzipperImpl implements Unzipper {

    @Value("${extract.destination}")
    private String destination;

    @Override
    public void unzip(String source) throws ZipException {
        try {
            String zipFilename = source.split("\\.")[0];
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination + zipFilename);
        } catch (ZipException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
