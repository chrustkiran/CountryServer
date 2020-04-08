package com.example.Country.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface FileUploader {
    Flux<DataBuffer> upload(FilePart filePart) throws IOException;
}
