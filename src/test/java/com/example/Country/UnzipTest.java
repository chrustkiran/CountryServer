package com.example.Country;

import com.example.Country.service.Unzipper;
import net.lingala.zip4j.exception.ZipException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UnzipTest {

   @Autowired
   Unzipper unzipper;

    @Test
    void unzip() throws ZipException {
     unzipper.unzip("example1.zip");
     assertThat(Files.exists(Paths.get("example1"))).isTrue();
    }

}
