package com.example.blog.service;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.Optional;

/**
 * Manages user profiles
 *
 * @author jitzerttok51
 */
public interface IUserService extends UserDetailsService {

    /**
     * Registers a new user profile to the service
     *
     * @param userCreate the user registration form
     *
     * @return the newly created user profile fetched from the persistent storage
     */
    UserDTO register(UserCreateDTO userCreate);

    /**
     * Fetch all user profiles from the resistant storage
     *
     * @return a collection of all registered users
     */
    Collection<UserDTO> getAllUsers();

    /**
     * Retrieve information about a single user profile
     *
     * @param id of the user profile
     *
     * @return the user profile
     */
    Optional<UserDTO> getUser(long id);

    /**
     * Delete the user profile
     *
     * @param id of the user profile
     *
     * @return the user profile of the deleted user
     */
    Optional<UserDTO> deleteUser(long id);

    /**
     * Edit a user profile
     *
     * @param id of the user to be edited
     * @param userCreate contains the new information about this user
     *
     * @return the updated user profile
     */
    Optional<UserDTO> editUser(long id, UserCreateDTO userCreate);

    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findByEmail(String email);

    boolean validatePassword(String username, String password);
}
