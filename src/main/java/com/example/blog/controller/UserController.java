package com.example.blog.controller;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserCreateDTO userCreate) {
        var result = userService.register(userCreate);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }

    @GetMapping
    public Collection<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable long id) {
        return ResponseEntity.of(userService.getUser(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable long id) {
        return ResponseEntity.of(userService.deleteUser(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDTO> editUser(@PathVariable long id, @RequestBody UserCreateDTO userCreate) {
        return ResponseEntity.of(userService.editUser(id, userCreate));
    }
}
