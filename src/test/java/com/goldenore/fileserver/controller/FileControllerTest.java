package com.goldenore.fileserver.controller;


import com.goldenore.fileserver.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
public class FileControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @InjectMocks
    private FileController fileController;
    @Mock
    private StorageService storageService;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

    @WithMockUser("user1")
    @Test
    public void whenUserLogInShouldReturnFilePage() throws Exception{
            mvc.perform(get("/drive/my_drive").contentType("text/html"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("file"));
    }

    @WithMockUser("user1")
    @Test
    public void whenNotProvidingDirectoryPathShouldRedirectToMainPage() throws Exception {
        mvc.perform(get("/drive/folders"))
                .andExpect(redirectedUrl("/drive/my_drive"))
                .andExpect(status().is3xxRedirection());
    }


}
