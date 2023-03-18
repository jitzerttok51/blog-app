package com.example.blog.controller;

import com.example.blog.entity.Dummy;
import com.example.blog.repositories.DummyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final DummyRepository repository;

    @Override
    public void run(String... args) throws Exception {
        var dummy = new Dummy();
        dummy.setData("Test123");
        repository.save(dummy);
        System.out.println(repository.findAll());
    }
}
