package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.TaskDAO;
import edu.yb.strtgst.entity.Task;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.dao.SQLUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TaskDAOImpl implements TaskDAO {

    @Override
    public boolean addEntity(Task entity) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Tasks VALUES (?, ?, ?, ?, ?)",
                entity.getTaskId(),
                entity.getTaskName(),
                entity.getTaskDescription(),
                entity.getDueDate(),
                entity.getStatus()
        );
    }

    @Override
    public Task getEntity(String id) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM Tasks WHERE task_id = ?", id);
        if (rst.next()) {
            return new Task(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDate(4).toLocalDate(),
                    rst.getString(5)
            );
        }
        return null;
    }

    @Override
    public boolean updateEntity(Task entity) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Tasks SET task_name = ?, task_description = ?, due_date = ?, status = ? WHERE task_id = ?",
                entity.getTaskName(),
                entity.getTaskDescription(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getTaskId()
        );
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return SQLUtil.execute("DELETE FROM Tasks WHERE task_id = ?", id);
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        return "";
    }

    @Override
    public ArrayList<Task> getAll() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM Tasks");

        ArrayList<Task> tasks = new ArrayList<>();
        while (rst.next()) {
            Task task = new Task(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDate(4).toLocalDate(),
                    rst.getString(5)
            );
            tasks.add(task);
        }

        return tasks;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        try {
            return IdLoader.getNextID(tableName, columnName);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Task ID");
            e.printStackTrace();
        }
        return "T001";
    }

    @Override
    public ArrayList<ArrayList> getAllTaskStatus() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT status, due_date, task_id FROM Tasks");
        ArrayList<ArrayList> list = new ArrayList<>();

        while (rst.next()) {
            ArrayList<String> row = new ArrayList<>();
            row.add(rst.getString("status"));
            row.add(rst.getString("due_date"));
            row.add(rst.getString("task_id"));
            list.add(row);
        }
        return list;
    }

    @Override
    public boolean updateTaskStatus(String taskId, String newStatus) throws SQLException {
        return SQLUtil.execute(
                "UPDATE Tasks SET status = ? WHERE task_id = ?",
                newStatus, taskId
        );
    }

    @Override
    public String getPendingOrOverdueTaskCount() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT COUNT(*) FROM Tasks WHERE status = 'Pending' OR status = 'Overdue'");
        if (rst.next()){
            return rst.getString(1);
        }
        return "0";
    }
}
