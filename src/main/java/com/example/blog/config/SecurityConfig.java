package com.example.blog.config;

import com.example.blog.filter.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.example.blog.constants.URLConstants.*;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${storage.local.location}")
    private String storageLocation;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JWTFilter filter) throws Exception {
        return http.csrf().disable()
                   .cors().disable()
                   .httpBasic().disable()
                   .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                   .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, ENDPOINT_USERS, ENDPOINT_AUTH).permitAll()
            .requestMatchers(ENDPOINT_STORAGE+"/**", "/*", ENDPOINT_ACTUATOR+"/**").permitAll()
            .requestMatchers(ENDPOINT_API).authenticated()
            .anyRequest().authenticated()
            .and().httpBasic()
            .and().build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(ENDPOINT_STORAGE+"/**")
            .addResourceLocations(storageLocation);

        registry
            .addResourceHandler("/*")
            .addResourceLocations("classpath:/frontend/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "index.html");
    }
}
