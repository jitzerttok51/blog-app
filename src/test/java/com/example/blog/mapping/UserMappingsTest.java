package com.example.blog.mapping;

import com.example.blog.config.ModelMapperConfig;
import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.entity.User;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMappingsTest {

    private static final ModelMapper MAPPER = new ModelMapperConfig().modelMapper();

    @Test
    public void testUserCreateDTOToUser() {
        var dto = new UserCreateDTO();
        dto.setEmail("email");
        dto.setUsername("username");
        var e = MAPPER.map(dto, User.class);
        assertEquals(e.getEmail(), dto.getEmail());
        assertEquals(e.getUsername(), dto.getUsername());
    }

    @Test
    public void testUserToUserDTO() {
        var e = new User();
        e.setEmail("email");
        e.setUsername("username");
        e.setId(13L);
        e.setCreatedDate(LocalDateTime.now());
        e.setModifiedDate(LocalDateTime.now());
        var dto = MAPPER.map(e, UserDTO.class);
        assertEquals(dto.getEmail(), e.getEmail());
        assertEquals(dto.getUsername(), e.getUsername());
        assertEquals(dto.getId(), e.getId());
        assertEquals(dto.getModifiedDate(), e.getModifiedDate());
        assertEquals(dto.getCreatedDate(), e.getCreatedDate());
    }
}
