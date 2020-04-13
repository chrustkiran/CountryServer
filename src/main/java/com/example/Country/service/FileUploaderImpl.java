package com.example.Country.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileUploaderImpl implements FileUploader {

    Logger logger = LoggerFactory.getLogger(FileUploaderImpl.class);

    @Value("${extract.destination}")
    private String destination;

    @Override
    public Mono<String> upload(FilePart filePart) throws IOException {

        final Mono<String> monoString = Mono.empty();
        Path path = Paths.get(destination+filePart.filename());
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        AsynchronousFileChannel channel =
                AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
        return DataBufferUtils.write(filePart.content(), channel, 0)
                .then(Mono.just(filePart.filename()));
    }
}
