package com.conference.exception;

import jakarta.ws.rs.NotFoundException;

public class SessionNotFoundException extends NotFoundException {
    public SessionNotFoundException(String id) {
        super("Session with ID " + id + " not found");
    }
}
