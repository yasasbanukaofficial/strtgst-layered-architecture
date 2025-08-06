package edu.yb.strtgst.controller;

import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class IntroPageController implements Initializable {
    public MediaView mediaViewer;
    public AnchorPane ancIntro;
    public Label lblMain;
    public Label lblComment;
    public Button btnExit;

    private final AppContext appContext = AppContext.getInstance();

    public Button btnLogin;
    public Button btnSignUp;
    public AnchorPane ancForms;

    public void visitDashboard() {
        Navigation.navigateTo(ancIntro, View.MAIN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playBackgroundVideo();
        setVisibility(false);
        appContext.setIntroPageController(this);
    }

    private void setVisibility(boolean visibility) {
        ancForms.setVisible(visibility);
    }

    private void playBackgroundVideo() {
        try {
            String videoPath = getClass().getResource("/videos/dirt.mp4").toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setMute(true);
            mediaPlayer.play();
            mediaViewer.toBack();
            mediaViewer.fitWidthProperty().bind(ancIntro.widthProperty());
            mediaViewer.fitHeightProperty().bind(ancIntro.heightProperty());
            mediaViewer.setPreserveRatio(false);
            mediaPlayer.setOnReady(() -> {
                mediaViewer.setOpacity(1);
            });

            mediaViewer.setMediaPlayer(mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoginFields(ActionEvent actionEvent) {
        btnSignUp.setDisable(false);
        lblMain.setText("LOGIN");
        lblComment.setText("Enter your username and password to continue.");
        setVisibility(true);
        Navigation.navigateTo(ancForms, View.LOGIN_FORM);
        btnLogin.setDisable(true);
    }

    public void showSignUpFields(ActionEvent actionEvent) {
        btnLogin.setDisable(false);
        lblMain.setText("SIGNUP");
        lblComment.setText("Create your account and start the journey.");
        setVisibility(true);
        Navigation.navigateTo(ancForms, View.SIGNUP_FORM);
        btnSignUp.setDisable(true);
    }

    public void closeApp(ActionEvent actionEvent) {
        Stage stage = (Stage) ancIntro.getScene().getWindow();
        stage.close();
    }
}
