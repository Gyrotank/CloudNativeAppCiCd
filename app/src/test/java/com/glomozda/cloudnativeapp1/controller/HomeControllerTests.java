package com.glomozda.cloudnativeapp1.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Tags({@Tag("IntegrationTest"), @Tag("ControllerTest"), @Tag("HomeControllerTest")})
@AutoConfigureMockMvc
class HomeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetHealthCheckStatus_OK() throws Exception {
        //Act and Assert
        mockMvc.perform(get("/")
                        .contentType(MediaType.ALL)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Health Check Passed"));
    }
}