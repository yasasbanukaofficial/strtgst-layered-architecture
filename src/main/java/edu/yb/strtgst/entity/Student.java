package edu.yb.strtgst.entity;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Student implements Serializable {
    private String studId;
    private String studName;
    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Student(String studId, String username, String email, String password) {
        this.studId = studId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Student(String studId, String studName, String username, String email, String password) {
        this.studId = studId;
        this.studName = studName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Student(String studId, String studName, String username, String email, String password, LocalDate dateOfBirth) {
        this.studId = studId;
        this.studName = studName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }
}
