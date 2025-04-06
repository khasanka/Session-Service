package com.conference.api;

import com.conference.db.SessionDAO;
import com.conference.model.SessionDTO;
import com.conference.service.SessionService;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionDTOResourceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDTOResourceTest.class);
    private static final SessionDAO sessionDAO = mock(SessionDAO.class);

    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addResource(new SessionController(new SessionService(sessionDAO)))
            .build();

    private SessionDTO sessionDTO;

    @BeforeAll
    void beforeAll() throws Throwable {
        LOGGER.info("Starting ResourceExtension for test suite");
        RESOURCES.before();
    }

    @AfterAll
    void afterAll() throws Throwable {
        LOGGER.info("Stopping ResourceExtension after tests");
        RESOURCES.after();
    }

    @BeforeEach
    void setup() {
        sessionDTO = new SessionDTO();
        sessionDTO.setId("123e4567-e89b-12d3-a456-426614174000");
        sessionDTO.setTitle("Sample Session");
        sessionDTO.setDescription("Desc");
        sessionDTO.setSpeaker("Speaker");
        sessionDTO.setFileUrl("http://example.com/file.pdf");
    }

    @AfterEach
    void tearDown() {
        reset(sessionDAO);
    }

    @Test
    void testGetSessions() {
        LOGGER.info("Testing GET /sessions");
        when(sessionDAO.getAll()).thenReturn(Collections.singletonList(sessionDTO));

        Response response = RESOURCES.target("/sessions")
                .request()
                .get();

        assertEquals(200, response.getStatus());

        List<SessionDTO> sessionDTOS = response.readEntity(new GenericType<>() {
        });
        assertNotNull(sessionDTOS);
        assertEquals(1, sessionDTOS.size());
        assertEquals("Sample Session", sessionDTOS.get(0).getTitle());
    }

    @Test
    void testCreateSession() {
        LOGGER.info("Testing POST /sessions with title: {}", sessionDTO.getTitle());
        doNothing().when(sessionDAO).insert(
                eq(sessionDTO.getId()),
                eq(sessionDTO.getTitle()),
                eq(sessionDTO.getDescription()),
                eq(sessionDTO.getSpeaker()),
                eq(sessionDTO.getFileUrl())
        );

        Response response = RESOURCES.target("/sessions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(201, response.getStatus());

        SessionDTO returned = response.readEntity(SessionDTO.class);
        assertNotNull(returned);
        assertNotNull(returned.getId());
        assertEquals(sessionDTO.getTitle(), returned.getTitle());
    }

    @Test
    void testUpdateSession() {
        LOGGER.info("Testing PUT /sessions/{}", sessionDTO.getId());
        when(sessionDAO.getById(eq(sessionDTO.getId()))).thenReturn(sessionDTO);
        doNothing().when(sessionDAO).update(eq(sessionDTO));

        Response response = RESOURCES.target("/sessions/" + sessionDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());
    }

    @Test
    void testDeleteSession() {
        LOGGER.info("Testing DELETE /sessions/{}", sessionDTO.getId());
        when(sessionDAO.delete(eq(sessionDTO.getId()))).thenReturn(1);

        Response response = RESOURCES.target("/sessions/" + sessionDTO.getId())
                .request()
                .delete();

        assertEquals(200, response.getStatus());
    }


    @Test
    void testCreateSessionWithMissingTitle() {
        sessionDTO.setTitle(null);
        LOGGER.warn("Testing POST /sessions with missing title");

        Response response = RESOURCES.target("/sessions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(422, response.getStatus());
    }

    @Test
    void testCreateSessionWithMissingSpeaker() {
        sessionDTO.setSpeaker(null);
        LOGGER.warn("Testing POST /sessions with missing speaker");

        Response response = RESOURCES.target("/sessions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(422, response.getStatus());
    }

    @Test
    void testCreateSessionWithInvalidFileUrl() {
        sessionDTO.setFileUrl("not-a-url");
        LOGGER.warn("Testing POST /sessions with invalid file URL");

        Response response = RESOURCES.target("/sessions")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(422, response.getStatus());
    }

    @Test
    void testUpdateSessionWithMissingTitle() {
        sessionDTO.setTitle(null);
        LOGGER.warn("Testing PUT /sessions/{} with missing title", sessionDTO.getId());
        when(sessionDAO.getById(eq(sessionDTO.getId()))).thenReturn(sessionDTO);

        Response response = RESOURCES.target("/sessions/" + sessionDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(422, response.getStatus());
    }

    @Test
    void testUpdateSessionWithInvalidFileUrl() {
        sessionDTO.setFileUrl("invalid-url");
        LOGGER.warn("Testing PUT /sessions/{} with invalid file URL", sessionDTO.getId());
        when(sessionDAO.getById(eq(sessionDTO.getId()))).thenReturn(sessionDTO);

        Response response = RESOURCES.target("/sessions/" + sessionDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(422, response.getStatus());
    }

    @Test
    void testUpdateSessionNotFound() {
        LOGGER.warn("Testing PUT /sessions/{} with non-existent ID", sessionDTO.getId());
        when(sessionDAO.getById(eq(sessionDTO.getId()))).thenReturn(null);

        Response response = RESOURCES.target("/sessions/" + sessionDTO.getId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(sessionDTO, MediaType.APPLICATION_JSON));

        assertEquals(404, response.getStatus());
    }

}
