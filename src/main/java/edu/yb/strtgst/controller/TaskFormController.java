package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.TaskBO;
import edu.yb.strtgst.controller.context.AppContext;
import edu.yb.strtgst.dto.TaskDto;
import edu.yb.strtgst.view.TaskTM;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class TaskFormController implements Initializable {
    public AnchorPane ancAddNewTask;
    public Label labelTaskHeader;
    public Label labelCancel;
    public TextField txtTaskName;
    public TextArea txtTaskDescription;
    public DatePicker dpTaskDueDate;
    public ComboBox <String> cmbStatus;
    public Button btnCancelTask;
    public Button btnAddTask;

    private final TaskBO taskBO = (TaskBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.TASK);
    private final AppContext appContext = AppContext.getInstance();
    private final TaskPageController taskPageController = appContext.getTaskPageController();

    private ObservableList<String> statusOptions = FXCollections.observableArrayList("Pending", "Completed", "Overdue");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appContext.setTaskFormController(this);
        setupFormDefaults();
    }

    public void cancelTask(ActionEvent actionEvent) {
        Navigation.navigateTo(ancAddNewTask, View.DEFAULT_TASK);
    }

    public void addTask(ActionEvent event) {
        String task_id = loadNextID();
        String taskName = txtTaskName.getText();
        String taskDescription = txtTaskDescription.getText();
        LocalDate dueDate = dpTaskDueDate.getValue();
        String status = cmbStatus.getValue();
        setButtonStates(false);
        if (validateTaskFields(taskName, status, dueDate)) {
            try {
                if (taskBO.addTask(new TaskDto(
                        task_id,
                        taskName,
                        taskDescription,
                        dueDate,
                        status
                ))) {
                    AlertUtil.setInfoAlert("Successfully added a task");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_TASK);
                } else AlertUtil.setErrorAlert("Failed to save the task");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when adding a task");
                e.printStackTrace();
            }
            taskPageController.updateOverdueStatus();
        }

    }

    private void deleteTask(TaskTM taskTM) {
        Optional<ButtonType> resp = AlertUtil.setConfirmationAlert("Are you sure you want to delete this task?",
                "Name: " + taskTM.getTaskName() + "\n" +
                        "Due: " + taskTM.getDueDate() + "\n" +
                        "Status: " + taskTM.getStatus());

        if (resp.isPresent() && resp.get() == ButtonType.YES) {
            String taskId = taskTM.getTaskId();
            try {
                if (taskBO.deleteTask(taskId)) {
                    taskPageController.setupTableColumn();
                    setupFormDefaults();
                    AlertUtil.setInfoAlert("Successfully deleted the task");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_TASK);
                    taskPageController.updateOverdueStatus();
                } else { AlertUtil.setErrorAlert("Failed to deleted the task"); }
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Error when deleting the task");
                e.printStackTrace();
            }
        }
    }

    private void editTask(TaskTM taskTM) {
        String task_id = taskTM.getTaskId();
        String taskName = txtTaskName.getText();
        String taskDescription = txtTaskDescription.getText();
        LocalDate dueDate = dpTaskDueDate.getValue();
        String status = cmbStatus.getValue();

        setButtonStates(false);
        if (validateTaskFields(taskName, status, dueDate)) {
            try {
                if (taskBO.editTask(new TaskDto(
                        task_id,
                        taskName,
                        taskDescription,
                        dueDate,
                        status
                ))) {
                    AlertUtil.setInfoAlert("Successfully edited the task");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_TASK);
                } else AlertUtil.setErrorAlert("Failed to edit the task");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when editing the task");
                e.printStackTrace();
            }
            taskPageController.updateOverdueStatus();
        }
    }

    public void populateFormForEdit(TaskTM taskTM){
        txtTaskName.setText(taskTM.getTaskName());
        txtTaskDescription.setText(taskTM.getTaskDescription());
        dpTaskDueDate.setValue(taskTM.getDueDate());
        cmbStatus.setValue(taskTM.getStatus());
        setButtonStates(false);

        labelTaskHeader.setText("Edit Task");
        labelCancel.setText("Cancel Editing");
        labelCancel.setOnMouseClicked(e -> {
            cancelTask(new ActionEvent());
        });

        btnAddTask.setText("Edit Task");
        btnAddTask.setOnAction(e -> {
            editTask(taskTM);});

        btnCancelTask.setText("Delete Task");
        btnCancelTask.getStyleClass().add("button-delete");
        btnCancelTask.setOnAction(e -> {deleteTask(taskTM);});
    }

    public String loadNextID(){
        return taskBO.loadNextID("Tasks", "task_id");
    }

    private void setupFormDefaults() {
        txtTaskName.setText("");
        txtTaskDescription.setText("");
        dpTaskDueDate.setValue(null);
        cmbStatus.setItems(statusOptions);
        cmbStatus.setValue(statusOptions.get(0));
        setButtonStates(false);
    }

    private void setButtonStates(boolean state) {
        btnAddTask.setDisable(state);
        btnCancelTask.setDisable(state);
    }

    private boolean validateTaskFields(String taskName, String status, LocalDate dueDate) {
        if (!areRequiredFieldsFilled(taskName, status, dueDate)){
            AlertUtil.setErrorAlert("You must fill required fields (*)!");
            return false;
        }

        if (!status.equals("Overdue") && dueDate.isBefore(LocalDate.now())){
            AlertUtil.setErrorAlert("Tasks due before today must be marked as overdue. ");
            return false;
        } else if (status.equals("Overdue") && dueDate.isAfter(LocalDate.now())){
            AlertUtil.setErrorAlert("Cannot mark a future task as overdue.");
            return false;
        }
        return true;
    }

    private boolean areRequiredFieldsFilled(Object... inputs) {
        for(Object input : inputs){
            if (input == null || input.equals("")) {
                return false;
            }
        }
        return true;
    }
}