package com.conference.db;

import com.conference.model.SessionDTO;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.List;

public interface SessionDAO {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS sessions (id VARCHAR(36) PRIMARY KEY, title VARCHAR(255), description TEXT, speaker VARCHAR(255), fileUrl TEXT)")
    void createTable();

    @SqlUpdate("INSERT INTO sessions (id, title, description, speaker, fileUrl) VALUES (:id, :title, :description, :speaker, :fileUrl)")
    void insert(@Bind("id") String id,
                @Bind("title") String title,
                @Bind("description") String description,
                @Bind("speaker") String speaker,
                @Bind("fileUrl") String fileUrl);

    @SqlQuery("SELECT * FROM sessions")
    @RegisterBeanMapper(SessionDTO.class)
    List<SessionDTO> getAll();

    @SqlQuery("SELECT * FROM sessions WHERE id = :id")
    @RegisterBeanMapper(SessionDTO.class)
    SessionDTO getById(@Bind("id") String id);

    @SqlUpdate("UPDATE sessions SET title = :title, description = :description, speaker = :speaker, fileUrl = :fileUrl WHERE id = :id")
    void update(@BindBean SessionDTO sessionDTO);

    @SqlUpdate("DELETE FROM sessions WHERE id = :id")
    int delete(@Bind("id") String id);
}
