package edu.yb.strtgst.controller;

import edu.yb.strtgst.context.AppContext;
import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.model.StudentModel;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.IdLoader;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class SignUpController {
    public AnchorPane ancSignUpForm;
    public VBox ancFields;
    public TextField txtUsername;
    public TextField txtEmail;
    public PasswordField txtPassword;

    private final StudentModel studentModel = new StudentModel();
    private final AppContext appContext = AppContext.getInstance();
    private final IntroPageController introPageController = appContext.getIntroPageController();
    private final String usernamePattern = "^[a-zA-Z0-9_-]{3,}$";
    private final String emailPattern = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
    private final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.\\-_*])[a-zA-Z0-9@#$%^&+=.\\-_]{6,}$";
    private final String errorStyle = "-fx-border-color: #ce0101; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
    private final String normalStyle = "-fx-border-color: #000000; -fx-background-color: transparent; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

    public void signUpUser(ActionEvent actionEvent) {
        String studentId = loadNextId();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        StudentDto studentDto = new StudentDto(studentId, username, email, password);

        if (validateInputs(username, email, password)) {
            try {
                if (studentModel.addStudent(studentDto)) {
                    AlertUtil.setInfoAlert("Successfully Saved user");
                    appContext.setUsername(username);
                    introPageController.visitDashboard();
                } else { AlertUtil.setErrorAlert("Failed when saving user"); }
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed when saving user");
                e.printStackTrace();
            }
        }
    }
    public String loadNextId() {
        try {
            return IdLoader.getNextID("Student", "stud_id");
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Student ID");
            e.printStackTrace();
        }
        return "S001";
    }

    private boolean validateInputs (String username, String email, String password) {
        StringBuilder errorMsg = new StringBuilder();
        boolean isValid = true;

        if (isUsernameTaken(username)) {
            errorMsg.append("• This username has already been taken \n");
            txtUsername.setStyle(errorStyle);
            isValid = false;
        } else if (!username.matches(usernamePattern)) {
            txtUsername.setStyle(errorStyle);
            errorMsg.append("• Username must have 3+ characters long and contain only letters, digits, underscores or hyphens.\n");
            isValid = false;
        } else txtUsername.setStyle(normalStyle);

        if (isEmailTaken(email)) {
            errorMsg.append("• This email has already been taken \n");
            txtEmail.setStyle(errorStyle);
            isValid = false;
        } else if (!email.matches(emailPattern)) {
            txtEmail.setStyle(errorStyle);
            errorMsg.append("• Email must be a valid one (e.g., name@example.com).\n");
            isValid = false;
        } else txtEmail.setStyle(normalStyle);

        if (!password.matches(passwordPattern)) {
            txtPassword.setStyle(errorStyle);
            errorMsg.append("• Password must be 6+ characters and should include following \n one (uppercase, lowercase, number, and special character).\n");
            isValid = false;
        } else txtPassword.setStyle(normalStyle);

        if (!isValid){
            AlertUtil.setErrorAlert("Error when creating an account: \n\n" + errorMsg);
        }
        return isValid;
    }

    private boolean isUsernameTaken(String username){
        try{
            return studentModel.fetchExistingUsername(username);
        } catch (SQLException e){
            AlertUtil.setErrorAlert("Error when checking if username exists.");
            e.printStackTrace();
        }
        return true;
    }

    private boolean isEmailTaken(String email){
        try{
            return studentModel.fetchExistingEmail(email);
        } catch (SQLException e){
            AlertUtil.setErrorAlert("Error when checking if email exists.");
            e.printStackTrace();
        }
        return true;
    }
}
