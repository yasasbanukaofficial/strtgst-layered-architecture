package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AssignmentBO;
import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.dto.AssignmentDto;
import edu.yb.strtgst.view.tm.AssignmentTM;
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

public class AssignmentPageController implements Initializable {
    public AnchorPane ancTaskContainer;
    public Label labelDate;
    public TableView <AssignmentTM> tblAssignment;
    public TableColumn <AssignmentTM, String> columnAssignmentName;
    public TableColumn <AssignmentTM, LocalDate> columnAssignmentDueDate;
    public TableColumn <AssignmentTM, String> columnAssignmentStatus;
    public TableColumn <AssignmentTM, String> columnAssignmentMarks;
    public TableView <AssignmentTM> tblCompletedAssignment;
    public TableColumn <AssignmentTM, String> columnCompletedName;
    public TableColumn <AssignmentTM, LocalDate> columnCompletedDueDate;
    public TableColumn <AssignmentTM, String> columnCompletedStatus;
    public TableColumn <AssignmentTM, String> columnCompletedMarks;

    private final AppContext appContext = AppContext.getInstance();

    AssignmentBO assignmentBO = (AssignmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ASSIGNMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumn();
        updateOverdueStatus();
        updateDateLabel();
        appContext.setAssignmentPageController(this);
        Navigation.navigateTo(ancTaskContainer, View.DEFAULT_ASSIGNMENT);
    }

    public void addNewAssignment(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_ASSIGNMENT);
    }

    public void setupTableColumn() {
        columnAssignmentName.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        columnAssignmentDueDate.setCellValueFactory(new PropertyValueFactory<>("assignmentDueDate"));
        columnAssignmentStatus.setCellValueFactory(new PropertyValueFactory<>("assignmentStatus"));
        columnAssignmentMarks.setCellValueFactory(new PropertyValueFactory<>("assignmentMarks"));

        columnAssignmentStatus.setCellFactory(c -> new TableCell<>(){
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

        columnCompletedName.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        columnCompletedDueDate.setCellValueFactory(new PropertyValueFactory<>("assignmentDueDate"));
        columnCompletedStatus.setCellValueFactory(new PropertyValueFactory<>("assignmentStatus"));
        columnCompletedMarks.setCellValueFactory(new PropertyValueFactory<>("assignmentMarks"));

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
            ArrayList<AssignmentDto> allAssignments = assignmentBO.getAllAssignments();
            tblAssignment.setItems(FXCollections.observableArrayList(
                    allAssignments.stream().filter(assignmentDto -> !assignmentDto.getAssignmentStatus().equalsIgnoreCase("Completed"))
                        .map(assignmentDto -> new AssignmentTM(
                                assignmentDto.getAssignmentId(),
                                assignmentDto.getAssignmentName(),
                                assignmentDto.getAssignmentDescription(),
                                assignmentDto.getAssignmentMarks(),
                                assignmentDto.getSubName(),
                                assignmentDto.getDueDate(),
                                assignmentDto.getAssignmentStatus()
                        )
                ).toList()
            ));

            tblCompletedAssignment.setItems(FXCollections.observableArrayList(
                    allAssignments.stream().filter(assignmentDto -> assignmentDto.getAssignmentStatus().equalsIgnoreCase("Completed"))
                            .map(assignmentDto -> new AssignmentTM(
                                    assignmentDto.getAssignmentId(),
                                    assignmentDto.getAssignmentName(),
                                    assignmentDto.getAssignmentDescription(),
                                    assignmentDto.getAssignmentMarks(),
                                    assignmentDto.getSubName(),
                                    assignmentDto.getDueDate(),
                                    assignmentDto.getAssignmentStatus()
                            )
                    ).toList()
            ));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when loading table data");
            e.printStackTrace();
        }
    }

    private String getStatusStyle(String status) {
        return switch (status.toLowerCase()) {
            case "completed" -> "-fx-background-color: #11C759; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            case "pending" -> "-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            case "overdue" -> "-fx-background-color: #d90429; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;";
            default -> "-fx-background-color: #bdc3c7; -fx-text-fill: black; -fx-padding: 4 8; -fx-background-radius: 10;";
        };
    }

    public void onClickAssignmentTable(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_ASSIGNMENT);
        AssignmentTM selectedAssignment = tblAssignment.getSelectionModel().getSelectedItem();
        AssignmentFormController assignmentFormController = appContext.getAssignmentFormController();
        if (selectedAssignment != null){
            assignmentFormController.populateFormForEdit(selectedAssignment);
        }
    }

    public void onClickCompletedTable(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancTaskContainer, View.ADD_ASSIGNMENT);
        AssignmentTM selectedAssignment = tblCompletedAssignment.getSelectionModel().getSelectedItem();
        AssignmentFormController assignmentFormController = appContext.getAssignmentFormController();
        if (selectedAssignment != null){
            assignmentFormController.populateFormForEdit(selectedAssignment);
        }
    }

    public void updateOverdueStatus() {
        try {
            LocalDate today = LocalDate.now();
            ArrayList<ArrayList> assignments = assignmentBO.getAllAssignmentStatus();

            for (ArrayList row : assignments) {
                String status = row.get(0).toString();
                LocalDate dueDate = LocalDate.parse(row.get(1).toString());
                String assignmentId = row.get(2).toString();

                if (status.equals("Pending") && dueDate.isBefore(today)) {
                    assignmentBO.updateAssignmentStatus(assignmentId, "Overdue");
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
}
