package com.example.blog.service.impl;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.repositories.UserRepository;
import com.example.blog.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserRepository repository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public UserDTO register(UserCreateDTO userCreate) {
        var entity = mapper.map(userCreate, User.class);
        entity.setHash("123");
        // TODO: Save password sha
        entity = repository.save(entity);
        return mapper.map(entity, UserDTO.class);
    }

    @Override
    public Collection<UserDTO> getAllUsers() {
        return repository
            .findAll()
            .stream()
            .map(this::toUserDTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<UserDTO> getUser(long id) {
        return repository
            .findById(id)
            .map(this::toUserDTO);
    }

    @Override
    @Transactional
    public Optional<UserDTO> deleteUser(long id) {
        return repository
            .findById(id)
            .map(this::deleteUser);
    }

    @Override
    @Transactional
    public Optional<UserDTO> editUser(long id, UserCreateDTO userCreate) {
        return repository
            .findById(id)
            .map(user -> this.updateUser(user, userCreate));
    }

    public UserDTO deleteUser(User user) {
        repository.delete(user);
        return toUserDTO(user);
    }

    public UserDTO updateUser(User user, UserCreateDTO userCreate) {
        user.setUsername(userCreate.getUsername());
        user.setEmail(user.getEmail());
        // TODO: Add update for password
        repository.save(user);
        return toUserDTO(user);
    }

    private UserDTO toUserDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }
}
