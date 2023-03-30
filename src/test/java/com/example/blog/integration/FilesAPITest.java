package com.example.blog.integration;

import com.example.blog.config.JacksonConfig;
import com.example.blog.constants.FileVisibility;
import com.example.blog.dto.ArtifactDTO;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.dto.FileEditDTO;
import com.example.blog.dto.UserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
public class FilesAPITest extends AbstractIntegrationTest {

    public static final String URL_USER_FILE_INFO = "/users/{userId}/files/{fileId}/info";

    public static final String URL_USER_FILE = "/users/{userId}/files/{fileId}";

    public static final String URL_USER_FILES = "/users/{userId}/files";

    public static final String URL_USERS = "/users";

    public static final String URL_AUTH = "/auth";

    public static final String FILE_CHECKSUM = "f78d380cd038c2fbb13a9e56478904492419ebfdf8c56cca92793b9534388937";

    public static final String FILE_CONTENT_TYPE = "image/jpeg";

    public static final String FILE_TYPE = "image";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JacksonConfig config;

    private static final String USER1 = "admin";

    private static final String USER2 = "admin2";

    private static final String PASSWORD = "TesT123456789";

    private static long user1ID = 0;

    private static long user2ID = 0;

    private static AuthResponseDTO user1AccessToken;

    private static AuthResponseDTO user2AccessToken;

    private static long pic1Id = 0;

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
    public void createUsers() throws Exception {

        user1ID = createUser(USER1);
        user2ID = createUser(USER2);
        user1AccessToken = getAccessToken(USER1);
        user2AccessToken = getAccessToken(USER2);

        Thread.sleep(1000);
    }

    @Test
    @Order(3)
    public void uploadImage() throws Exception {
        setAccessToken(user1AccessToken);

        var pic = getPicture("image.jpg");


        var request = multipart(URL_USER_FILES, user1ID)
            .file(pic);

        var artifact = preform(request, HttpStatus.CREATED, ArtifactDTO.class);
        pic1Id = artifact.getId();
        assertImage(artifact, FileVisibility.PRIVATE);
    }

    @Test
    @Order(4)
    public void accessFromUser1() throws Exception {
        setAccessToken(user1AccessToken);

        var request = get(URL_USER_FILE_INFO, user1ID, pic1Id);

        var artifact = preform(request, HttpStatus.OK, ArtifactDTO.class);
        assertImage(artifact, FileVisibility.PRIVATE);
    }

    @Test
    @Order(5)
    public void accessFromUser2ShouldFail() throws Exception {
        setAccessToken(user2AccessToken);

        var request = get(URL_USER_FILE_INFO, user1ID, pic1Id);

        preform(request, HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(6)
    public void setPublic() throws Exception {
        setAccessToken(user1AccessToken);

        var reqBody = new FileEditDTO();
        reqBody.setVisibility(FileVisibility.PUBLIC);

        var request = put(URL_USER_FILE, user1ID, pic1Id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(reqBody));

        var artifact = preform(request, HttpStatus.OK, ArtifactDTO.class);
        assertImage(artifact, FileVisibility.PUBLIC);
    }

    @Test
    @Order(7)
    public void accessFromUser2ShouldSucceed() throws Exception {
        setAccessToken(user2AccessToken);

        var request = get(URL_USER_FILE_INFO, user1ID, pic1Id);

        var artifact = preform(request, HttpStatus.OK, ArtifactDTO.class);
        assertImage(artifact, FileVisibility.PUBLIC);
    }

    @Test
    @Order(8)
    public void getImagesForUser1() throws Exception {
        setAccessToken(user1AccessToken);

        var request = get(URL_USER_FILES, user1ID);

        var artifacts = preform(request, HttpStatus.OK, new TypeReference<List<ArtifactDTO>>() {});
        assertFalse(artifacts.isEmpty());
        assertImage(artifacts.get(0), FileVisibility.PUBLIC);
    }

    @Test
    @Order(9)
    public void user2CanSeeUser1sFiles() throws Exception {
        setAccessToken(user2AccessToken);

        var request = get(URL_USER_FILES, user1ID);

        var artifacts = preform(request, HttpStatus.OK, new TypeReference<List<ArtifactDTO>>() {});
        assertFalse(artifacts.isEmpty());
        assertImage(artifacts.get(0), FileVisibility.PUBLIC);
    }

    @Test
    @Order(10)
    public void getImagesForUser2() throws Exception {
        setAccessToken(user2AccessToken);

        var request = get(URL_USER_FILES, user2ID);

        var artifacts = preform(request, HttpStatus.OK, new TypeReference<List<ArtifactDTO>>() {});
        assertTrue(artifacts.isEmpty());
    }

    @Test
    @Order(11)
    public void uploadSecondImage() throws Exception {
        setAccessToken(user1AccessToken);

        var pic = getPicture("cat.jpg");


        var request = multipart(URL_USER_FILES, user1ID)
            .file(pic);

        var artifact = preform(request, HttpStatus.CREATED, ArtifactDTO.class);
        assertCat(artifact, FileVisibility.PRIVATE);
    }

    @Test
    @Order(12)
    public void user1CanSeeAllTheirImages() throws Exception {
        setAccessToken(user1AccessToken);

        var request = get(URL_USER_FILES, user1ID);

        var artifacts = preform(request, HttpStatus.OK, new TypeReference<List<ArtifactDTO>>() {});
        assertEquals(2, artifacts.size());
    }

    @Test
    @Order(13)
    public void user2CanSeeOnlyPublicImages() throws Exception {
        setAccessToken(user2AccessToken);

        var request = get(URL_USER_FILES, user1ID);

        var artifacts = preform(request, HttpStatus.OK, new TypeReference<List<ArtifactDTO>>() {});
        assertEquals(1, artifacts.size());
    }

    @Test
    @Order(14)
    public void user2TryDeleteUser1PublicImage() throws Exception {
        setAccessToken(user2AccessToken);

        var request = delete(URL_USER_FILE, user1ID, pic1Id);

        preform(request, HttpStatus.NOT_FOUND);
    }

    private void assertImage(ArtifactDTO artifact, FileVisibility visibility) {
        assertEquals(artifact.getOwner(), USER1);
        assertEquals(artifact.getSha256(), FILE_CHECKSUM);
        assertEquals(artifact.getFileType(), FILE_CONTENT_TYPE);
        assertEquals(artifact.getType(), FILE_TYPE);
        assertEquals(artifact.getVisibility(), visibility);
    }

    private void assertCat(ArtifactDTO artifact, FileVisibility visibility) {
        assertEquals(artifact.getOwner(), USER1);
        assertEquals(artifact.getSha256(), "1c2746bef0ac1dfa97732d7b6c0659367f4d9572a875cd91dba64011985e8aea");
        assertEquals(artifact.getFileType(), FILE_CONTENT_TYPE);
        assertEquals(artifact.getType(), FILE_TYPE);
        assertEquals(artifact.getVisibility(), visibility);
    }

    private MockMultipartFile getPicture(String pic) throws IOException {
        try(var in = ClassLoader.getSystemResourceAsStream("images/"+pic)) {
            return new MockMultipartFile("file", pic, FILE_CONTENT_TYPE, in);
        }
    }

    private long createUser(String username) throws Exception {
        var requestBody = basicUser(username, PASSWORD);

        var request = post(URL_USERS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        var body = preform(request, HttpStatus.CREATED, UserDTO.class);

        return body.getId();
    }

    private AuthResponseDTO getAccessToken(String username) throws Exception {
        var requestBody = loginDTO(username, PASSWORD);

        var request = post(URL_AUTH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(getMapper().writeValueAsString(requestBody));

        var body = preform(request, HttpStatus.OK, AuthResponseDTO.class);
        assertNotNull(body);
        return body;
    }
}
