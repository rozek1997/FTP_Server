package com.goldenore.fileserver;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileserverApplicationTests {

    @LocalServerPort
    int serverPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {

        assertThat(this.restTemplate.getForObject(String.format("http://localhost:%d/", serverPort), String.class)).contains("HelloWorld");
    }

}
