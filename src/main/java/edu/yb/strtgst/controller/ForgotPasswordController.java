package edu.yb.strtgst.controller;

import edu.yb.strtgst.util.AuthenticationUtil;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

public class ForgotPasswordController {
    public AnchorPane ancOtpPage;
    public Label lblHeader;
    public TextField txtUserEmail;
    public TextField txtOtpCode;

    public AnchorPane ancUpdatePassword;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;

    private int otpCode;
    private static String userEmail = "";
    private final String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.\\-_*])[a-zA-Z0-9@#$%^&+=.\\-_]{6,}$";
    private final String errorStyle = "-fx-border-color: #ce0101; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";
    private final String normalStyle = "-fx-border-color: #000000; -fx-border-radius: 10px; -fx-border-width: 2px; -fx-background-radius: 10px";

    public void sendOTP(ActionEvent actionEvent) {
        userEmail = txtUserEmail.getText().trim();
        Random random = new Random();
        otpCode = random.nextInt(9000) + 1000;
        try {
            boolean isEmailExisting = AuthenticationUtil.checkIfEmailExisting(userEmail);
            if (!isEmailExisting){
                AlertUtil.setErrorAlert("Your email was not found in the database. Please try again!");
                return;
            }

            final String systemEmail = "yasasbanukaofficial@gmail.com";
            final String password = "oqcw kvhm scad ifis";

            String host = "smtp.gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(systemEmail, password);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(systemEmail));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
                message.setSubject("OTP Code for Forgot Password is " + otpCode);
                message.setText("Your otp code is " + otpCode + " never share it with anyone. \n Enjoy using Strtgst to your heart's content...");

                Transport.send(message);

                lblHeader.setText("OTP has been sent.");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when checking if email exists.");
        }
    }

    public void visitUpdatePassword(ActionEvent actionEvent) {
        if (txtOtpCode.getText().trim().isEmpty()){
            AlertUtil.setErrorAlert("Please enter OTP code.");
            return;
        }
        if (Integer.parseInt(txtOtpCode.getText()) == otpCode){
            Navigation.navigateTo(ancOtpPage, View.UPDATE_PASSWORD);
        } else {
            AlertUtil.setErrorAlert("Your OTP code is incorrect. Please try again!");
        }
    }

    public void updatePassword(String password) {
        try {
            boolean isPasswordUpdated = AuthenticationUtil.updatePassword(password, userEmail);
            if (isPasswordUpdated){
                AlertUtil.setInfoAlert("Password has been updated successfully.");
                Stage stage = (Stage) ancUpdatePassword.getScene().getWindow();
                stage.close();
            } else {
                AlertUtil.setErrorAlert("Error when updating password. Please try again!");
            }
        } catch (SQLException e) {
            AlertUtil.setInfoAlert("Error when updating password. Please try again!");
            e.printStackTrace();
        }
    }

    public void validatePassword(ActionEvent actionEvent) {
        String password = txtPassword.getText().trim();
        if (!password.matches(passwordPattern)) {
            txtPassword.setStyle(errorStyle);
            AlertUtil.setErrorAlert("Password must be 6+ characters and should include following one (uppercase, lowercase, number, and special character).");

        } else {
            if (!txtPassword.getText().trim().equals(txtConfirmPassword.getText().trim())){
                AlertUtil.setErrorAlert("Passwords do not match.");
            } else {
                txtPassword.setStyle(normalStyle);
                updatePassword(password);
            }
        }
    }
}
