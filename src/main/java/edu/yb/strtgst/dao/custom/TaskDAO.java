package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaskDAO extends CrudDAO<Task> {
    ArrayList<ArrayList> getAllTaskStatus() throws SQLException;
    boolean updateTaskStatus(String taskId, String newStatus) throws SQLException;
    String getPendingOrOverdueTaskCount() throws SQLException;
}
