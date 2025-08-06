package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.StudentDto;

import java.sql.SQLException;

public interface StudentBO extends SuperBO {
    boolean addStudent(StudentDto studentDto) throws SQLException;

    StudentDto getStudent(String username) throws SQLException;

    boolean fetchExistingUsername(String username) throws SQLException;

    boolean fetchExistingEmail(String email) throws SQLException;

    StudentDto fetchStudentDetails(String username) throws SQLException;

    String fetchStudentName(String username) throws SQLException;

    boolean updateStudent(StudentDto studentDto) throws SQLException;

    String getStudentIdByUsername(String username) throws SQLException;
}
