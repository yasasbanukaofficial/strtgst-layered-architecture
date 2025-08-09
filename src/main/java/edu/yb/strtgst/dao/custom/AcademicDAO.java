package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Academic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface AcademicDAO extends CrudDAO<Academic> {
    boolean syncEntryByAi(String query) throws SQLException;
    Academic getRecentDetails(String tableName) throws SQLException;
    public String getResponse(String instructions);
    public String buildSqlInsertPrompt(String userInput);
    public String askAboutStudies(String userInput, StringBuilder previousMsg);
    public String reminderGenerator();
}
