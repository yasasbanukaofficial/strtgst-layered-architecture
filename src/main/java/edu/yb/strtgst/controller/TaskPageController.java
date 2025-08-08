package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.TaskBO;
import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.dto.TaskDto;
import edu.yb.strtgst.dto.tm.TaskTM;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.DateUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TaskPageController implements Initializable {
    public AnchorPane ancTaskContainer;
    public TableView <TaskTM> tblTask;
    public TableColumn <TaskTM, String> columnTaskName;
    public TableColumn <TaskTM, LocalDate> columnTaskDueDate;
    public TableColumn <TaskTM, String> columnTaskStatus;
    public TableView <TaskTM> tblCompletedTask;
    public TableColumn <TaskTM, String> columnCompletedName;
    public TableColumn <TaskTM, LocalDate> columnCompletedDueDate;
    public TableColumn <TaskTM, String> columnCompletedStatus;
    public Label labelDate;

    private final TaskBO taskBO = (TaskBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.TASK);
    private final AppContext appContext = AppContext.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumn();
        updateOverdueStatus();
        updateDateLabel();
        appContext.setTaskPageController(this);
        Navigation.navigateTo(ancTaskContainer, View.DEFAULT_TASK);
    }

    public void addNewTask(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_TASK);
    }

    public void setupTableColumn() {
        columnTaskName.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        columnTaskDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        columnTaskStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        columnTaskStatus.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if(empty || status == null){
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(status);
                    label.setStyle(getStatusStyle(status));
                    setGraphic(label);
                    setText(null);
                }
            }
        });

        columnCompletedName.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        columnCompletedDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        columnCompletedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        columnCompletedStatus.setCellFactory(c -> new TableCell<>(){
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if(empty || status == null){
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(status);
                    label.setStyle(getStatusStyle(status));
                    setGraphic(label);
                    setText(null);
                }
            }
        });

        loadTableData();
    }

    private void loadTableData(){
        try {
            ArrayList<TaskDto> allTasks = taskBO.getAllTasks();
            tblTask.setItems(FXCollections.observableArrayList(
                    allTasks.stream().filter(taskDto -> !taskDto.getStatus().equalsIgnoreCase("Completed"))
                            .map(taskDto -> new TaskTM(
                                            taskDto.getTaskId(),
                                            taskDto.getTaskName(),
                                            taskDto.getTaskDescription(),
                                            taskDto.getDueDate(),
                                            taskDto.getStatus()
                                    )
                            ).toList()
            ));

            tblCompletedTask.setItems(FXCollections.observableArrayList(
                    allTasks.stream().filter(taskDto -> taskDto.getStatus().equalsIgnoreCase("Completed"))
                            .map(taskDto -> new TaskTM(
                                            taskDto.getTaskId(),
                                            taskDto.getTaskName(),
                                            taskDto.getTaskDescription(),
                                            taskDto.getDueDate(),
                                            taskDto.getStatus()
                                    )
                            ).toList()
            ));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when loading table data");
            e.printStackTrace();
        }
    }

    public void onClickTaskTable(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_TASK);
        TaskTM selectedTask = tblTask.getSelectionModel().getSelectedItem();
        TaskFormController taskFormController = appContext.getTaskFormController();
        if (selectedTask != null){
            taskFormController.populateFormForEdit(selectedTask);
        }
    }

    public void onClickCompletedTable(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_TASK);
        TaskTM selectedTask = tblCompletedTask.getSelectionModel().getSelectedItem();
        TaskFormController taskFormController = appContext.getTaskFormController();
        if (selectedTask != null){
            taskFormController.populateFormForEdit(selectedTask);
        }
    }

    public void updateOverdueStatus() {
        try {
            LocalDate today = LocalDate.now();
            ArrayList<ArrayList> tasks = taskBO.getAllTaskStatus();

            for (ArrayList row : tasks) {
                String status = row.get(0).toString();
                LocalDate dueDate = LocalDate.parse(row.get(1).toString());
                String taskId = row.get(2).toString();

                if (status.equals("Pending") && dueDate.isBefore(today)) {
                    taskBO.updateTaskStatus(taskId, "Overdue");
                }
            }
            loadTableData();

        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when updating status");
            e.printStackTrace();
        }
    }

    private void updateDateLabel() {
        labelDate.setText(DateUtil.setDate());
    }

    private String getStatusStyle(String status) {
        return switch (status.toLowerCase()) {
            case "completed" -> "-fx-background-color: #11C759; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            case "pending" -> "-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            case "overdue" -> "-fx-background-color: #d90429; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            default -> "-fx-background-color: #bdc3c7; -fx-text-fill: black; -fx-padding: 4 8; -fx-background-radius: 10;";
        };
    }
}
