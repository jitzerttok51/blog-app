package com.example.blog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Autowired
    MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    public ObjectMapper getMapper() {
        return springMvcJacksonConverter.getObjectMapper();
    }
}
