package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    public AnchorPane ancLoginForm;
    public TextField txtUsername;
    public PasswordField txtPassword;

    private final AppContext appContext = AppContext.getInstance();
    private final IntroPageController introPageController = appContext.getIntroPageController();
    public Button btnForgotPassword;

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOType.STUDENT);

    public void loginUser(ActionEvent actionEvent) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.equals("") || password.equals("")) {
            AlertUtil.setErrorAlert("Please enter username and password.");
            return;
        }

        try {
            if (validateCredentials(username, password)) {
                appContext.setUsername(username);
                introPageController.visitDashboard();
            } else {
                AlertUtil.setErrorAlert("Invalid username or password. Please Try again!");
                showLoginError();
            }
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Something went wrong while trying to log in.");
            e.printStackTrace();
        }
    }

    private void showLoginError() {
        String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
        txtUsername.setStyle(errorStyle);
        txtPassword.setStyle(errorStyle);
    }

    public boolean validateCredentials(String username, String password) throws Exception {
        StudentDto studentDto = studentBO.getStudent(username);
        return studentDto != null && password.equals(studentDto.getPassword());
    }

    public void forgotPassword(MouseEvent mouseEvent) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(View.FORGOT_PASSWORD.getPath()));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertUtil.setErrorAlert("Something went wrong while trying to navigate to Forgot Password page.");
            e.printStackTrace();
        }
    }
}
