package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AssignmentBO;
import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.dto.AssignmentDto;
import edu.yb.strtgst.dto.SubjectDto;
import edu.yb.strtgst.dto.tm.AssignmentTM;
import edu.yb.strtgst.model.AcademicModel;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.IdLoader;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssignmentFormController implements Initializable {
    public AnchorPane ancAddNewTask;
    public TextField txtAssignmentName;
    public TextArea txtAssignmentDescription;
    public ComboBox<String> cmbSubject;
    public DatePicker dpDueDate;
    public TextField txtMarks;
    public ComboBox<String> cmbStatus;
    public Button btnAddAssignment;
    public Button btnCancel;
    public Label labelAssignmentHeader;
    public Label labelCancel;

    private AssignmentDto assignmentDto;
    private final AppContext appContext = AppContext.getInstance();
    private final AssignmentPageController assignmentPageController = appContext.getAssignmentPageController();
    private final AcademicModel academicModel = new AcademicModel();

    private ObservableList<String> statusOptions = FXCollections.observableArrayList("Pending", "Completed", "Overdue");
    private ObservableList<String> subjectOptions = FXCollections.observableArrayList();

    AssignmentBO assignmentBO = (AssignmentBO) BOFactory.getInstance().getBO(BOFactory.BOType.ASSIGNMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appContext.setAssignmentFormController(this);
        loadSubjects();
        setupFormDefaults();
    }

    private void loadSubjects() {
        try {
            ArrayList<SubjectDto> subjects = academicModel.getAllSubjects();
            for (SubjectDto dto : subjects) {
                subjectOptions.add(dto.getSubName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (subjectOptions.isEmpty()) {
            AlertUtil.setErrorAlert("Please add some subjects before adding an assignment.");
            return;
        }
    }

    public void cancelTask(ActionEvent actionEvent) {
        Navigation.navigateTo(ancAddNewTask, View.DEFAULT_ASSIGNMENT);
    }

    public void addAssignment(ActionEvent event) {
        String assignment_id = assignmentBO.loadNextID("Assignment", "assignment_id");
        String assignmentName = txtAssignmentName.getText();
        String assignmentDescription = txtAssignmentDescription.getText();
        String assignmentMarks = txtMarks.getText();
        String subName = cmbSubject.getValue();
        LocalDate date = dpDueDate.getValue();
        String status = cmbStatus.getValue();

        setButtonStates(false);
        if (validateAssignmentFields(assignmentName, status, assignmentMarks, date)) {
            assignmentDto = new AssignmentDto(
                    assignment_id,
                    assignmentName,
                    assignmentDescription,
                    assignmentMarks,
                    subName,
                    date,
                    status
            );

            try {
                if (assignmentBO.addAssignment(assignmentDto)) {
                    AlertUtil.setInfoAlert("Successfully added an assignment");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_ASSIGNMENT);
                } else AlertUtil.setErrorAlert("Failed to save an Assignment");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when adding an assignment");
                e.printStackTrace();
            }
            assignmentPageController.updateOverdueStatus();
        }
    }

    private void deleteAssignment(AssignmentTM assignmentTM) {
        Optional<ButtonType> resp = AlertUtil.setConfirmationAlert("Are you sure you want to delete this assignment?",
                "Name: " + assignmentTM.getAssignmentName() + "\n" +
                "Due: " + assignmentTM.getAssignmentDueDate() + "\n" +
                "Status: " + assignmentTM.getAssignmentStatus() + "\n" +
                "Marks: " + assignmentTM.getAssignmentMarks());

        if (resp.isPresent() && resp.get() == ButtonType.YES) {
            String assignmentId = assignmentTM.getAssignmentId();
            try {
                if (assignmentBO.deleteAssignment(assignmentId)) {
                    assignmentPageController.setupTableColumn();
                    setupFormDefaults();
                    AlertUtil.setInfoAlert("Successfully deleted an assignment");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_ASSIGNMENT);
                } else { AlertUtil.setErrorAlert("Failed to deleted an assignment"); }
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Error when deleting an assignment");
                e.printStackTrace();
            }
        }
    }

    private void editAssignment(AssignmentTM assignmentTM) {
        String assignment_id = assignmentTM.getAssignmentId();
        String assignmentName = txtAssignmentName.getText();
        String assignmentDescription = txtAssignmentDescription.getText();
        String assignmentMarks = txtMarks.getText();
        LocalDate date = dpDueDate.getValue();
        String status = cmbStatus.getValue();
        String subName = cmbSubject.getValue();

        setButtonStates(false);
        if (validateAssignmentFields(assignmentName, status, assignmentMarks, date)) {
            assignmentDto = new AssignmentDto(
                    assignment_id,
                    assignmentName,
                    assignmentDescription,
                    assignmentMarks,
                    subName,
                    date,
                    status
            );

            try {
                if (assignmentBO.editAssignment(assignmentDto)) {
                    AlertUtil.setInfoAlert("Successfully edited the assignment");
                    Navigation.navigateTo(ancAddNewTask, View.DEFAULT_ASSIGNMENT);
                } else AlertUtil.setErrorAlert("Failed to edit the assignment");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when editing the assignment");
                e.printStackTrace();
            }
            assignmentPageController.updateOverdueStatus();
        }
    }

    public void populateFormForEdit(AssignmentTM assignmentTM){
        txtAssignmentName.setText(assignmentTM.getAssignmentName());
        txtAssignmentDescription.setText(assignmentTM.getAssignmentDescription());
        dpDueDate.setValue(assignmentTM.getAssignmentDueDate());
        cmbStatus.setValue(assignmentTM.getAssignmentStatus());
        txtMarks.setText(assignmentTM.getAssignmentMarks());
        cmbSubject.setValue(assignmentTM.getSubName());
        setButtonStates(false);

        labelAssignmentHeader.setText("Edit Assignment");
        labelCancel.setText("Cancel Editing");
        labelCancel.setOnMouseClicked(e -> {
            cancelTask(new ActionEvent());
        });

        btnAddAssignment.setText("Edit Assignment");
        btnAddAssignment.setOnAction(e -> {editAssignment(assignmentTM);});

        btnCancel.setText("Delete Assignment");
        btnCancel.getStyleClass().add("button-delete");
        btnCancel.setOnAction(e -> {deleteAssignment(assignmentTM);});
    }

    private void setupFormDefaults() {
        txtAssignmentName.setText("");
        txtAssignmentDescription.setText("");
        dpDueDate.setValue(null);
        cmbSubject.setItems(subjectOptions);
        cmbSubject.setValue(subjectOptions.get(0));
        cmbStatus.setItems(statusOptions);
        cmbStatus.setValue(statusOptions.get(0));
        setButtonStates(false);
    }

    private void setButtonStates(boolean state) {
        btnAddAssignment.setDisable(state);
        btnCancel.setDisable(state);
    }

    private boolean validateAssignmentFields(String assignmentName, String status, String marks, LocalDate date) {
        if (!areRequiredFieldsFilled(assignmentName, status, date)){
            AlertUtil.setErrorAlert("You must fill required fields (*)!");
            return false;
        }

        if (Integer.parseInt(marks) < 0 || Integer.parseInt(marks) > 100){
            AlertUtil.setErrorAlert("Marks must be between 0 and 100.");
            return false;
        }

        if (status.equals("Overdue") && date.isAfter(LocalDate.now())){
            AlertUtil.setErrorAlert("Cannot mark a future assignment as overdue.");
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