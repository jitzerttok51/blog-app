/*
 * Copyright (c) 2004-2005 Apama Ltd.
 * Copyright (c) 2006-2012 Progress Software Corporation
 * Copyright (c) 2014-2022 Software AG, Darmstadt, Germany and/or
 * Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries
 * and/or its affiliates and/or their licensors. Use, reproduction,
 * transfer, publication or disclosure is prohibited except as
 * specifically provided for in your License Agreement with Software AG.
 */

package com.example.blog.integration;

import com.example.blog.dto.AuthDTO;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.dto.UserCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AbstractIntegrationTest {

    private static PostgreSQLContainer<?> POSTGRES_SQL_CONTAINER;

    private ObjectMapper mapper;

    private MockMvc mockMvc;

    private static String accessToken = "";

    @BeforeAll
    public static void createDB() {
        POSTGRES_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("blog_app");
        POSTGRES_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void overrideTestProperties(DynamicPropertyRegistry registry) {

        System.out.println(POSTGRES_SQL_CONTAINER.getJdbcUrl());
        System.out.println(POSTGRES_SQL_CONTAINER.getUsername());
        System.out.println(POSTGRES_SQL_CONTAINER.getPassword());

        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SQL_CONTAINER::getPassword);
    }

    @AfterAll
    public static void destroyDB() {
        POSTGRES_SQL_CONTAINER.stop();
    }

    protected void setAccessToken(AuthResponseDTO body) {
        accessToken = body.getType() + " " + body.getAccessToken();
    }

    protected void init(MockMvc mockMvc, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public String getAccessToken() {
        return accessToken;
    }

    protected <T> T preform(MockHttpServletRequestBuilder request, HttpStatus status, Class<T> clazz) throws Exception {
        if(!accessToken.isBlank()) {
            request = request.header("Authorization", accessToken);
        }
        var response = this.mockMvc
            .perform(request)
            .andDo(print())
            .andExpect(status().is(status.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, clazz);
    }

    protected void preform(MockHttpServletRequestBuilder request, HttpStatus status) throws Exception {
        if(!accessToken.isBlank()) {
            request = request.header("Authorization", accessToken);
        }
        this.mockMvc
            .perform(request)
            .andDo(print())
            .andExpect(status().is(status.value()));
    }

    protected UserCreateDTO basicUser(String username, String password) {
        var requestBody = new UserCreateDTO();

        requestBody.setUsername(username);
        requestBody.setEmail(username+"@gmail.com");
        requestBody.setPassword(password);
        requestBody.setConfirmPassword(password);

        return requestBody;
    }

    protected AuthDTO loginDTO(String username, String password) {
        var auth = new AuthDTO();
        auth.setUsername(username);
        auth.setPassword(password);
        return auth;
    }
}
