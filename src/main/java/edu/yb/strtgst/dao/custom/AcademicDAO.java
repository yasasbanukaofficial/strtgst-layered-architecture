package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Academic;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface AcademicDAO extends CrudDAO<Academic> {
    boolean syncEntryByAi(String query) throws SQLException;
    Academic getRecentDetails(String tableName) throws SQLException;
}
