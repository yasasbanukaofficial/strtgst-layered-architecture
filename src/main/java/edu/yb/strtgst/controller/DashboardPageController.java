package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AssignmentBO;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.bo.custom.TaskBO;
import edu.yb.strtgst.model.*;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.PromptBuilder;
import edu.yb.strtgst.util.View;
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

    private final TaskBO taskBO = (TaskBO) BOFactory.getInstance().getBO(BOFactory.BOType.TASK);
    private final CalendarModel calendarModel = new CalendarModel();
    public MediaView mediaViewer;
    private StringBuilder previousMsg = new StringBuilder();
    private final ChatBotModel chatBotModel = new ChatBotModel();
    private final PromptBuilder promptBuilder = new PromptBuilder();

    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOType.SUBJECT);
    AssignmentBO assignmentBO = (AssignmentBO) BOFactory.getInstance().getBO(BOFactory.BOType.ASSIGNMENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTexts();
        countTotal();
//        playBackgroundVideo();
    }

//    private void playBackgroundVideo() {
//        try {
//            String videoPath = getClass().getResource("/videos/dirt.mp4").toExternalForm();
//            Media media = new Media(videoPath);
//            MediaPlayer mediaPlayer = new MediaPlayer(media);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//            mediaPlayer.setMute(true);
//            mediaPlayer.play();
//            mediaViewer.toBack();
//            mediaViewer.fitWidthProperty().bind(ancDashboard.widthProperty());
//            mediaViewer.fitHeightProperty().bind(ancDashboard.heightProperty());
//            mediaViewer.setPreserveRatio(false);
//            mediaPlayer.setOnReady(() -> {
//                mediaViewer.setOpacity(1);
//            });
//
//            mediaViewer.setMediaPlayer(mediaPlayer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
            labelEventsToday.setText(calendarModel.getAllFutureEntries("Event"));
            labelLecturesToday.setText(calendarModel.getAllFutureEntries("Lecture"));
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
        String response = chatBotModel.getResponse(promptBuilder.askAboutStudies(userInput, previousMsg));
        Text userTxt = new Text("User:      " + userInput + "\n");
        Text responseTxt = new Text("Chat:      " + response);
        txtChatFlow.getChildren().add(userTxt);
        txtChatFlow.getChildren().add(responseTxt);
        previousMsg.append(userInput).append("\n");
        txtEnterMsg.setText("");
    }
}
