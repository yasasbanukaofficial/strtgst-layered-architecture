package edu.yb.strtgst.model;

import edu.yb.strtgst.dto.TaskDto;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TaskModel {
    public boolean addTask(TaskDto taskDto) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Tasks VALUES (?, ?, ?, ?, ?)",
                taskDto.getTaskId(),
                taskDto.getTaskName(),
                taskDto.getTaskDescription(),
                taskDto.getDueDate(),
                taskDto.getStatus()
        );
    }

    public boolean editTask(TaskDto taskDto) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Tasks SET task_name = ?, task_description = ?, due_date = ?, status = ? WHERE task_id = ?",
                taskDto.getTaskName(),
                taskDto.getTaskDescription(),
                taskDto.getDueDate(),
                taskDto.getStatus(),
                taskDto.getTaskId()
        );
    }

    public ArrayList<TaskDto> getAllTasks() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Tasks");

        ArrayList<TaskDto> taskDtos = new ArrayList<>();
        while (rst.next()) {
            TaskDto taskDto = new TaskDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDate(4).toLocalDate(),
                    rst.getString(5)
            );
            taskDtos.add(taskDto);
        }

        return taskDtos;
    }

    public ArrayList<ArrayList> getAllTaskStatus() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT status, due_date, task_id FROM Tasks");
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


    public boolean updateTaskStatus(String taskId, String newStatus) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Tasks SET status = ? WHERE task_id = ?",
                newStatus, taskId
        );
    }

    public boolean deleteTask(String taskId) throws SQLException {
        return CrudUtil.execute("DELETE FROM Tasks WHERE task_id = ?", taskId);
    }

    public String getPendingOrOverdueTaskCount() throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM Tasks WHERE status = 'Pending' OR status = 'Overdue'");
        if (rst.next()){
            return rst.getString(1);
        }
        return "0";
    }
}
