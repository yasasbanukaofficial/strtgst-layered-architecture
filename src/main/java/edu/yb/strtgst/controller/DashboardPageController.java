package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AcademicBO;
import edu.yb.strtgst.bo.custom.AssignmentBO;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.bo.custom.TaskBO;
import edu.yb.strtgst.util.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardPageController implements Initializable {
    public AnchorPane ancDashboard;
    public Label labelTotalTasks;
    public Label labelTotalAssignments;
    public Label labelEventsToday;
    public Label labelGpaCalculation;
    public Label labelLecturesToday;

    public VBox ancChatBot;
    public TextField txtEnterMsg;
    public TextFlow txtChatFlow;
    public StackPane btnSendMsg;

    private final CalendarUtil calendarUtil = new CalendarUtil();
    private StringBuilder previousMsg = new StringBuilder();
    private final PromptBuilder promptBuilder = new PromptBuilder();

    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SUBJECT);
    AssignmentBO assignmentBO = (AssignmentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ASSIGNMENT);
    TaskBO taskBO = (TaskBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.TASK);
    AcademicBO academicBO = (AcademicBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ACADEMIC);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTexts();
        countTotal();
    }

    private void setUpTexts() {
        Text intialText = new Text("""
                ~  Welcome to Strtgst AI Bot
                
                -  Ask Anything
                •  Ask about Science (What is mitochondria?)
                •  Ask about History (Who is Napoleon Bonaparte?)
                """);
        txtChatFlow.getChildren().add(intialText);
        double gpa = subjectBO.getGPACalculation();
        labelGpaCalculation.setText(String.format("%.2f", gpa));
    }

    public void visitTasksPage(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancDashboard, View.TASK);
    }

    public void visitEventsPage(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancDashboard, View.CALENDAR);
    }

    public void visitAssignmentsPage(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancDashboard, View.ASSIGNMENT);
    }

    public void visitLecturesPage(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancDashboard, View.CALENDAR);
    }

    private void countTotal() {
        try {
            labelTotalTasks.setText(taskBO.getPendingOrOverdueTaskCount());
            labelTotalAssignments.setText(assignmentBO.getPendingOrOverdueAssignmentCount());
            labelEventsToday.setText(calendarUtil.getAllFutureEntries("Event"));
            labelLecturesToday.setText(calendarUtil.getAllFutureEntries("Lecture"));
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when fetching total");
            e.printStackTrace();
        }
    }

    public void sendMessage(MouseEvent mouseEvent) {
        txtChatFlow.getChildren().clear();
        String userInput = txtEnterMsg.getText();
        if (userInput.trim().equals("")) {
            AlertUtil.setErrorAlert("Please enter a valid entry message to send");
            return;
        }
        String response = academicBO.getResponse(promptBuilder.askAboutStudies(userInput, previousMsg));
        Text userTxt = new Text("User:      " + userInput + "\n");
        Text responseTxt = new Text("Chat:      " + response);
        txtChatFlow.getChildren().add(userTxt);
        txtChatFlow.getChildren().add(responseTxt);
        previousMsg.append(userInput).append("\n");
        txtEnterMsg.setText("");
    }
}
