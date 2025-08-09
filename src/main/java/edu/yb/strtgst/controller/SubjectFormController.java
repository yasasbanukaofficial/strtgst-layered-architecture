package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.controller.context.AppContext;
import edu.yb.strtgst.dto.SubjectDto;
import edu.yb.strtgst.view.SubjectTM;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;


public class SubjectFormController implements Initializable {

    public AnchorPane ancAddNewSubject;
    public Label labelSubjectHeader;
    public Label labelCancel;
    public TextField txtSubjectName;
    public TextArea txtSubjectDescription;
    public TextField txtMarks;
    public Button btnCancel;
    public Button btnAddSubject;

    private static final AppContext appContext = AppContext.getInstance();
    private final SubjectPageController subjectPageController = appContext.getSubjectPageController();
    private static String studId = "";

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);
    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SUBJECT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appContext.setSubjectFormController(this);
        setupFormDefaults();
        try {
            studId = studentBO.getStudentIdByUsername(appContext.getUsername());
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when finding student ID");
        }
    }

    private void setupFormDefaults() {
        labelSubjectHeader.setText("Add New Subject");
        labelCancel.setText("Cancel");
        btnAddSubject.setText("Add Subject");
        btnCancel.setText("Cancel");
        btnCancel.getStyleClass().remove("button-delete");

        txtSubjectName.clear();
        txtSubjectDescription.clear();
        txtMarks.clear();

        btnAddSubject.setOnAction(this::addNewSubject);
        btnCancel.setOnAction(this::cancelSubject);
    }

    private boolean validateSubjectFields(String marks) {
        if (txtSubjectName.getText().isEmpty()) {
            AlertUtil.setErrorAlert("Subject name cannot be empty");
            return false;
        }
        if (marks.isEmpty()) {
            AlertUtil.setErrorAlert("Subject marks cannot be empty");
            return false;
        }
        try {
            double value = Double.parseDouble(marks);
            if (value <= 0) {
                AlertUtil.setErrorAlert("Subject marks must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.setErrorAlert("Subject marks must be a valid number");
            return false;
        }
        return true;
    }

    private void setButtonStates(boolean isDisabled) {
        btnAddSubject.setDisable(isDisabled);
        btnCancel.setDisable(isDisabled);
    }

    public void cancelSubject(ActionEvent actionEvent) {
        Navigation.navigateTo(ancAddNewSubject, View.DEFAULT_SUBJECT);
    }

    public void addNewSubject(ActionEvent actionEvent) {
        String subId = subjectBO.loadNextID("Subject", "sub_Id");
        String subjectName = txtSubjectName.getText();
        String subjectDescription = txtSubjectDescription.getText();
        String subjectMarks = txtMarks.getText();

        setButtonStates(false);
        if (validateSubjectFields(subjectMarks)) {
            try {
                if (subjectBO.addSubject(new SubjectDto(
                        subId,
                        studId,
                        subjectName,
                        subjectDescription,
                        subjectMarks
                ))) {
                    AlertUtil.setInfoAlert("Successfully added a subject");
                    Navigation.navigateTo(ancAddNewSubject, View.DEFAULT_SUBJECT);
                    subjectPageController.loadTableData();
                } else AlertUtil.setErrorAlert("Failed to save a Subject");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when adding a subject");
                e.printStackTrace();
            }
        }
    }

    private void deleteSubject(SubjectTM subjectTM) {
        Optional<ButtonType> resp = AlertUtil.setConfirmationAlert("Are you sure you want to delete this subject?",
                "Name: " + subjectTM.getSubName() + "\n" +
                        "Marks: " + subjectTM.getTotalMarks());

        if (resp.isPresent() && resp.get() == ButtonType.YES) {
            String subId = subjectTM.getSubId();
            try {
                if (subjectBO.deleteSubject(subId)) {
                    subjectPageController.setupTableColumn();
                    setupFormDefaults();
                    AlertUtil.setInfoAlert("Successfully deleted a subject");
                    subjectPageController.loadTableData();
                    Navigation.navigateTo(ancAddNewSubject, View.DEFAULT_SUBJECT);
                } else { AlertUtil.setErrorAlert("Failed to deleted a subject"); }
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Error when deleting a subject");
                e.printStackTrace();
            }
        }
    }

    private void editSubject(SubjectTM subjectTM) {
        String subId = subjectTM.getSubId();
        String subjectName = txtSubjectName.getText();
        String subjectDescription = txtSubjectDescription.getText();
        String subjectMarks = txtMarks.getText();

        setButtonStates(false);
        if (validateSubjectFields(subjectMarks)) {
            try {
                if (subjectBO.editSubject(new SubjectDto(
                        subId,
                        studId,
                        subjectName,
                        subjectDescription,
                        subjectMarks
                ))) {
                    AlertUtil.setInfoAlert("Successfully edited the subject");
                    Navigation.navigateTo(ancAddNewSubject, View.DEFAULT_SUBJECT);
                    subjectPageController.loadTableData();
                } else AlertUtil.setErrorAlert("Failed to edit the subject");
            } catch (SQLException e) {
                AlertUtil.setErrorAlert("Failed when editing the subject");
                e.printStackTrace();
            }
        }
    }

    public void populateFormForEdit(SubjectTM subjectTM){
        txtSubjectName.setText(subjectTM.getSubName());
        txtSubjectDescription.setText(subjectTM.getSubDescription());
        txtMarks.setText(subjectTM.getTotalMarks());
        setButtonStates(false);

        labelSubjectHeader.setText("Edit Subject");
        labelCancel.setText("Cancel Editing");
        labelCancel.setOnMouseClicked(e -> {
            cancelSubject(new ActionEvent());
        });

        btnAddSubject.setText("Edit Subject");
        btnAddSubject.setOnAction(e -> {
            editSubject(subjectTM);
        });

        btnCancel.setText("Delete Subject");
        btnCancel.getStyleClass().add("button-delete");
        btnCancel.setOnAction(e -> {
            deleteSubject(subjectTM);
        });
    }
}
