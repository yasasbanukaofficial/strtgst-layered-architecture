package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.custom.TaskBO;
import edu.yb.strtgst.dao.DAOFactory;
import edu.yb.strtgst.dao.custom.AssignmentDAO;
import edu.yb.strtgst.dao.custom.TaskDAO;
import edu.yb.strtgst.dao.custom.impl.TaskDAOImpl;
import edu.yb.strtgst.dto.TaskDto;
import edu.yb.strtgst.entity.Task;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.SQLException;
import java.util.ArrayList;

public class TaskBOImpl implements TaskBO {
    TaskDAO taskDAO = (TaskDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTYPES.TASK);

    @Override
    public boolean addTask(TaskDto taskDto) throws SQLException {
        return taskDAO.addEntity(new Task(
                taskDto.getTaskId(),
                taskDto.getTaskName(),
                taskDto.getTaskDescription(),
                taskDto.getDueDate(),
                taskDto.getStatus()
        ));
    }

    @Override
    public boolean editTask(TaskDto taskDto) throws SQLException {
        return taskDAO.updateEntity(new Task(
                taskDto.getTaskId(),
                taskDto.getTaskName(),
                taskDto.getTaskDescription(),
                taskDto.getDueDate(),
                taskDto.getStatus()
        ));
    }

    @Override
    public boolean deleteTask(String taskId) throws SQLException {
        return taskDAO.deleteEntity(taskId);
    }

    @Override
    public ArrayList<TaskDto> getAllTasks() throws SQLException {
        ArrayList<Task> tasks = taskDAO.getAll();
        ArrayList<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : tasks) {
            taskDtos.add(new TaskDto(
                    task.getTaskId(),
                    task.getTaskName(),
                    task.getTaskDescription(),
                    task.getDueDate(),
                    task.getStatus()
            ));
        }
        return taskDtos;
    }

    @Override
    public ArrayList<ArrayList> getAllTaskStatus() throws SQLException {
        return taskDAO.getAllTaskStatus();
    }

    @Override
    public boolean updateTaskStatus(String taskId, String newStatus) throws SQLException {
        return taskDAO.updateTaskStatus(taskId, newStatus);
    }

    @Override
    public String getPendingOrOverdueTaskCount() throws SQLException {
        return taskDAO.getPendingOrOverdueTaskCount();
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
}
