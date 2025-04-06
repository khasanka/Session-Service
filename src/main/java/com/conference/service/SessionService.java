package com.conference.service;

import com.conference.db.SessionDAO;
import com.conference.exception.SessionNotFoundException;
import com.conference.model.SessionDTO;

import java.util.List;

public class SessionService {
    private final SessionDAO dao;

    public SessionService(SessionDAO dao) {
        this.dao = dao;
        dao.createTable();
    }

    public List<SessionDTO> getAll() {
        return dao.getAll();
    }

    public SessionDTO getById(String id) {
        SessionDTO sessionDTO = dao.getById(id);
        if (sessionDTO == null) throw new SessionNotFoundException(id);
        return sessionDTO;
    }

    public SessionDTO create(SessionDTO sessionDTO) {
        dao.insert(sessionDTO.getId(), sessionDTO.getTitle(), sessionDTO.getDescription(), sessionDTO.getSpeaker(), sessionDTO.getFileUrl());
        return sessionDTO;
    }

    public SessionDTO update(String id, SessionDTO sessionDTO) {
        if (dao.getById(id) == null) throw new SessionNotFoundException(id);
        dao.update(sessionDTO);
        return sessionDTO;
    }

    public SessionDTO delete(String id) {
        int deleted = dao.delete(id);
        if (deleted == 0) throw new SessionNotFoundException(id);
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(id);
        return sessionDTO;
    }

}