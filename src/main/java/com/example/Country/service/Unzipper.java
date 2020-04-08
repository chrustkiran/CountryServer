package com.example.Country.service;

import net.lingala.zip4j.exception.ZipException;

public interface Unzipper {
    void unzip(String source) throws ZipException;
}
