package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Student;

import java.sql.SQLException;

public interface StudentDAO extends CrudDAO<Student> {
    boolean fetchExistingUsername(String username) throws SQLException;

    boolean fetchExistingEmail(String email) throws SQLException;

    Student fetchStudentDetails(String username) throws SQLException;

    String fetchStudentName(String username) throws SQLException;
}
