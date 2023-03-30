package com.example.blog.integration;

import com.example.blog.config.JacksonConfig;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
public class AuthAPITest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JacksonConfig config;

    private static final String ADMIN_USERNAME = "admin";

    private static final String ADMIN_PASSWORD = "Test123456789";

    private static final String ADMIN_PASSWORD_CHANGED = "TesT123456789";

    private static long adminId = 0;

    @BeforeEach
    public void setup() {
        var mockMvc = MockMvcBuilders
            .webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity()).build();
        init(mockMvc, config.getMapper());
    }

    @Test
    @Order(1)
    public void checkControllers() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("userController"));
        assertNotNull(webApplicationContext.getBean("authController"));
    }

    @Test
    @Order(2)
    public void createAdminUser() throws Exception {
        var requestBody = basicUser(ADMIN_USERNAME, ADMIN_PASSWORD);

        var request = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        var body = preform(request, HttpStatus.CREATED, UserDTO.class);

        assertEquals(requestBody.getUsername(), body.getUsername());
        assertEquals(requestBody.getEmail(), body.getEmail());
        adminId = body.getId();
        Thread.sleep(1000);
    }

    @Test
    @Order(3)
    public void getAuthenticationToken() throws Exception {
        var requestBody = loginDTO(ADMIN_USERNAME, ADMIN_PASSWORD);

        var request = post("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        var body = preform(request, HttpStatus.OK, AuthResponseDTO.class);

        assertEquals("Bearer", body.getType());
        setAccessToken(body);
    }

    @Test
    @Order(4)
    public void testToken() throws Exception {
        var body = preform(get("/users"), HttpStatus.OK, UserDTO[].class);

        assertEquals(1, body.length);
        assertEquals(ADMIN_USERNAME, body[0].getUsername());
    }

    @Test
    @Order(5)
    public void changePassword() throws Exception {
        // Here we wait for the token to expire so that user is modified not at the JWT creation time
        // This only happens in Mock MVC because everything uses one JVM process there is no
        // client server traffic
        Thread.sleep(1000);
        var body = preform(get("/users/"+adminId), HttpStatus.OK, UserDTO.class);

        assertEquals(adminId, body.getId());
        assertEquals(ADMIN_USERNAME, body.getUsername());

        var requestBody = fromUserDTO(body);
        requestBody.setPassword(ADMIN_PASSWORD_CHANGED);
        requestBody.setConfirmPassword(ADMIN_PASSWORD_CHANGED);

        var request = put("/users/"+adminId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        body = preform(request, HttpStatus.OK, UserDTO.class);

        assertEquals(adminId, body.getId());
        assertEquals(ADMIN_USERNAME, body.getUsername());
    }

    @Test
    @Order(6)
    public void testTokenAfterPasswordChange() throws Exception {
        preform(get("/users"), HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(7)
    public void typUsingOldPassword() throws Exception {
        var requestBody = loginDTO(ADMIN_USERNAME, ADMIN_PASSWORD);

        var request = post("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        preform(request, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(8)
    public void getNewToken() throws Exception {
        var requestBody = loginDTO(ADMIN_USERNAME, ADMIN_PASSWORD_CHANGED);

        var request = post("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        var body = preform(request, HttpStatus.OK, AuthResponseDTO.class);

        assertEquals("Bearer", body.getType());
        setAccessToken(body);
    }

    @Test
    @Order(9)
    public void testNewToken() throws Exception {
        var body = preform(get("/users"), HttpStatus.OK, UserDTO[].class);

        assertEquals(1, body.length);
        assertEquals(ADMIN_USERNAME, body[0].getUsername());
    }

    @Test
    @Order(10)
    public void deleteAdminAccount() throws Exception {
        preform(delete("/users/"+adminId), HttpStatus.OK);
    }

    @Test
    @Order(11)
    public void testTokenAfterAccountDeletion() throws Exception {
        preform(get("/users"), HttpStatus.UNAUTHORIZED);
    }

    private UserCreateDTO fromUserDTO(UserDTO user) {
        var create = new UserCreateDTO();
        create.setEmail(user.getEmail());
        create.setUsername(user.getUsername());

        return create;
    }
}
