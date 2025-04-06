package com.conference.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);


    @Override
    public Response toResponse(Throwable ex) {

        if (ex instanceof SessionNotFoundException) {
            LOGGER.warn("Not found: {}", ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", ex.getMessage()))
                    .build();
        }


        LOGGER.error("Unhandled exception", ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Something went wrong. Please contact support.")
                .build();
    }
}
