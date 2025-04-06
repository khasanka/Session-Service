package com.conference.auth;

import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiKeyAuthFilterTest {

    private static final String VALID_KEY = "valid-api-key";
    private static final String INVALID_KEY = "invalid-api-key";

    @Path("/secured")
    public static class SecuredResource {
        @GET
        public Response secure() {
            return Response.ok("Access Granted").build();
        }
    }

    private final ApiKeyAuthFilter authFilter = new ApiKeyAuthFilter(VALID_KEY);

    private final ResourceExtension resources = ResourceExtension.builder()
            .addProvider(authFilter)
            .addResource(new SecuredResource())
            .build();

    @BeforeAll
    void setUp() throws Throwable {
        resources.before();
    }

    @AfterAll
    void tearDown() throws Throwable {
        resources.after();
    }

    @Test
    void testAccessWithValidApiKey() {
        Response response = resources.target("/secured")
                .request()
                .header("X-API-Key", VALID_KEY)
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Access Granted", response.readEntity(String.class));
    }

    @Test
    void testAccessWithInvalidApiKey() {
        Response response = resources.target("/secured")
                .request()
                .header("X-API-Key", INVALID_KEY)
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testAccessWithoutApiKey() {
        Response response = resources.target("/secured")
                .request()
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testAccessWithWhitespaceApiKey() {
        Response response = resources.target("/secured")
                .request()
                .header("X-API-Key", "   ")
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testAccessWithTrimmedValidKey() {
        Response response = resources.target("/secured")
                .request()
                .header("X-API-Key", "  valid-api-key  ")
                .get();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testAccessWithLowerCaseHeaderName() {
        Response response = resources.target("/secured")
                .request()
                .header("x-api-key", VALID_KEY)
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Access Granted", response.readEntity(String.class));
    }

    @Test
    void testAccessWithEmptyApiKey() {
        Response response = resources.target("/secured")
                .request()
                .header("X-API-Key", "")
                .get();

        assertEquals(401, response.getStatus());
    }
}
