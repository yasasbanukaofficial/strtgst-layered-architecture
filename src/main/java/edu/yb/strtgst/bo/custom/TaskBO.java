package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.TaskDto;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TaskBO extends SuperBO {
    boolean addTask(TaskDto taskDto) throws SQLException;
    boolean editTask(TaskDto taskDto) throws SQLException;
    boolean deleteTask(String taskId) throws SQLException;
    ArrayList<TaskDto> getAllTasks() throws SQLException;
    ArrayList<ArrayList> getAllTaskStatus() throws SQLException;
    boolean updateTaskStatus(String taskId, String newStatus) throws SQLException;
    String getPendingOrOverdueTaskCount() throws SQLException;
    String loadNextID(String tableName, String columnName);
}
