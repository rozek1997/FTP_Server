package com.goldenore.fileserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class HomeControllerTest {

    @Autowired
    private HomeController homeController;

    @Test
    public void cotextLoads(){
        assertThat(homeController, notNullValue());
    }

}
