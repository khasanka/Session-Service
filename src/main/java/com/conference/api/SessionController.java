package com.conference.api;

import com.conference.model.SessionDTO;
import com.conference.service.SessionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @GET
    public List<SessionDTO> list() {
        LOGGER.info("Listing all sessions");
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public SessionDTO get(@PathParam("id") String id) {
        LOGGER.info("Fetching session with ID: {}", id);
        return service.getById(id);
    }

    @POST
    public Response create(@Valid SessionDTO sessionDTO) {
        String id = UUID.randomUUID().toString();
        sessionDTO.setId(id);
        LOGGER.info("Creating session with title: {}", sessionDTO.getTitle());
        sessionDTO = service.create(sessionDTO);
        return Response.status(Response.Status.CREATED).entity(sessionDTO).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") String id, @Valid SessionDTO sessionDTO) {
        LOGGER.info("Updating session ID: {}", id);
        sessionDTO = service.update(id, sessionDTO);
        return Response.ok(sessionDTO).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        LOGGER.info("Deleting session ID: {}", id);
        SessionDTO sessionDTO = service.delete(id);
        return Response.status(Response.Status.OK).entity(sessionDTO).build();
    }
}
