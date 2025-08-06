package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
    public AnchorPane ancTabDisplay;
    public AnchorPane ancMainPage;
    public Label txtStudentName;
    public Label txtUsername;
    public MediaView mediaViewer;

    private AppContext appContext = AppContext.getInstance();

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appContext.setMainPageController(this);
        stopAndNavigate(ancTabDisplay, View.DASHBOARD);
        setUpLabels();
    }

    public void setUpLabels() {
        try {
            String username = appContext.getUsername();
            txtUsername.setText("@" + username);
            txtStudentName.setText(studentBO.fetchStudentName(username));
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when fetching student name and username");
            e.printStackTrace();
        }
    }

    private void stopAndNavigate(AnchorPane anchorPane, View view) {
        UpdateThread.stopThread();
        Navigation.navigateTo(anchorPane, view);
    }

    public void visitAssignmentPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.ASSIGNMENT);
    }

    public void visitDashboardPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.DASHBOARD);
    }

    public void visitTaskPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.TASK);
    }

    public void visitSettingsPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.SETTINGS);
    }

    public void visitCalendarPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.CALENDAR);
    }

    public void visitLoginPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancMainPage, View.INTRO);
    }

    public void visitSubjectPage(MouseEvent mouseEvent) {
        stopAndNavigate(ancTabDisplay, View.SUBJECT);
    }
}
