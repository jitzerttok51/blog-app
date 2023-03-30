package com.example.blog.service.impl;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import com.example.blog.exceptions.NotValidException;
import com.example.blog.repositories.UserRepository;
import com.example.blog.service.IUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserRepository repository;

    private final ModelMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final Validator validator;

    @Override
    @Transactional
    public UserDTO register(UserCreateDTO userCreate) {
        Set<ConstraintViolation<UserCreateDTO>> validations = validator.validate(userCreate);
        if(!validations.isEmpty()) {
            throw new NotValidException(validations);
        }
        var entity = mapper.map(userCreate, User.class);
        entity.setHash(passwordEncoder.encode(userCreate.getPassword()));
        entity = repository.save(entity);
        return mapper.map(entity, UserDTO.class);
    }

    @Override
    public Collection<UserDTO> getAllUsers() {
        return repository.findAll().stream()
            .map(this::toUserDTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<UserDTO> getUser(long id) {
        return repository.findById(id)
            .map(this::toUserDTO);
    }

    @Override
    @Transactional
    public Optional<UserDTO> deleteUser(long id) {
        return repository.findById(id)
            .map(this::deleteUser);
    }

    @Override
    @Transactional
    public Optional<UserDTO> editUser(long id, UserCreateDTO userCreate) {
        return repository.findById(id)
            .map(user -> this.updateUser(user, userCreate));
    }

    public UserDTO deleteUser(User user) {
        repository.delete(user);
        return toUserDTO(user);
    }

    public UserDTO updateUser(User user, UserCreateDTO userCreate) {
        if (userCreate.getPassword() != null) {
            user.setHash(passwordEncoder.encode(userCreate.getPassword()));
        }
        if (userCreate.getUsername() != null) {
            user.setUsername(userCreate.getUsername());
        }
        if (userCreate.getEmail() != null) {
            user.setEmail(userCreate.getEmail());
        }
        repository.save(user);
        return toUserDTO(user);
    }

    private UserDTO toUserDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository
            .findUserByUsername(username)
            .orElseThrow(() -> this.userNotFound(username));

        return toUserDetails(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username, LocalDateTime notModifiedAfter)
        throws UsernameNotFoundException {

        var user = repository
            .findUserByUsername(username)
            .filter(u->u.getModifiedDate().minusSeconds(1).isBefore(notModifiedAfter))
            .orElseThrow(() -> this.userNotFound(username));


        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHash(), List.of());
    }

    private UsernameNotFoundException userNotFound(String username) {
        return new UsernameNotFoundException("User with " + username + " was not found");
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return repository.findUserByUsername(username).map(this::toUserDTO);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return repository.findUserByEmail(email).map(this::toUserDTO);
    }

    public boolean validatePassword(String username, String password) {
        var user = repository.findUserByUsername(username);
        if(user.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(password, user.get().getHash());
    }
}
