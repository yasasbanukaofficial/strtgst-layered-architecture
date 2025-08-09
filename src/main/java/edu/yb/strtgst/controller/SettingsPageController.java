package edu.yb.strtgst.controller;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.controller.context.AppContext;
import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SettingsPageController implements Initializable {
    public AnchorPane ancSettings;
    public Label labelStudentName;
    public Label labelStudentEmail;
    public Label labelStudentAge;
    public TextField txtUsername;
    public TextField txtName;
    public DatePicker dpBirthDate;
    public TextField txtEmail;
    public TextField txtPassword;
    public Button btnSave;
    public Button btnCancel;

    private StudentDto studentDetails;
    private final MainPageController mainPageController = AppContext.getInstance().getMainPageController();
    private final String usernamePattern = "^[a-zA-Z0-9_-]{3,}$";
    private final String emailPattern = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";
    private final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.\\-_*])[a-zA-Z0-9@#$%^&+=.\\-_]{6,}$";
    private final String errorStyle = "-fx-border-color: #ce0101; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
    private final String normalStyle = "-fx-border-color: #000000; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

    StudentBO studentBO = (StudentBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.STUDENT);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeData();
    }

    private void setFieldDefaults(StudentDto studentDto){
        labelStudentName.setText(studentDto.getStudName());
        labelStudentEmail.setText(studentDto.getEmail());
        labelStudentAge.setText(getStudentAge(studentDto.getDateOfBirth()));
        txtUsername.setText(studentDto.getUsername());
        txtName.setText(studentDto.getStudName());
        txtEmail.setText(studentDto.getEmail());
        txtPassword.setText(studentDto.getPassword());
        dpBirthDate.setValue(studentDto.getDateOfBirth());
    }

    private void initializeData() {
        try {
            studentDetails = studentBO.fetchStudentDetails(AppContext.getInstance().getUsername());
            if (studentDetails == null) {
                AlertUtil.setErrorAlert("Error when fetching student details");
                return;
            }
            setFieldDefaults(studentDetails);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when fetching student details");
            e.printStackTrace();
        }
    }

    public void saveChanges(ActionEvent event) {
        String studentId = studentDetails.getStudId();
        String username = txtUsername.getText();
        String name = txtName.getText();
        LocalDate dateOfBirth = dpBirthDate.getValue();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (validateInputs(username, email, password, dateOfBirth)) {
            StudentDto studentDto = new StudentDto(
                    studentId,
                    name,
                    username,
                    email,
                    password,
                    dateOfBirth
            );
            try {
                if (studentBO.updateStudent(studentDto)) {
                    AlertUtil.setInfoAlert("Successfully made changes");
                    AppContext.getInstance().setUsername(username);
                    mainPageController.setUpLabels();
                    setFieldDefaults(studentDto);
                    this.studentDetails = studentDto;
                } else { AlertUtil.setErrorAlert("Failed when making changes"); }
            } catch (Exception e) {
                AlertUtil.setErrorAlert("Failed when making changes");
                e.printStackTrace();
            }
        }
    }

    private String getStudentAge(LocalDate birthDate) {
        int age = LocalDate.now().getYear() - birthDate.getYear();
        if (birthDate.getMonthValue() > LocalDate.now().getMonthValue() || birthDate.getDayOfYear() > LocalDate.now().getDayOfYear()) {
            --age;
            return String.valueOf(age);
        }
        return String.valueOf(age);
    }

    public void cancelSaveChanges(ActionEvent event) {
    }

    private boolean validateInputs (String username, String email, String password, LocalDate dateOfBirth) {
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
            return studentBO.fetchExistingUsername(username) && !studentDetails.getUsername().equals(username);
        } catch (SQLException e){
            AlertUtil.setErrorAlert("Error when checking if username exists.");
            e.printStackTrace();
        }
        return true;
    }

    private boolean isEmailTaken(String email){
        try{
            return studentBO.fetchExistingEmail(email) && !studentDetails.getEmail().equals(email);
        } catch (SQLException e){
            AlertUtil.setErrorAlert("Error when checking if email exists.");
            e.printStackTrace();
        }
        return true;
    }
}
