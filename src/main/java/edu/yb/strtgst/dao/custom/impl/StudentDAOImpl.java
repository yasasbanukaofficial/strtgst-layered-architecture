package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.StudentDAO;
import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.entity.Student;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentDAOImpl implements StudentDAO {
    @Override
    public boolean addEntity(Student student) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Student (stud_id, username, email, password) VALUES (?,?,?,?)",
                student.getStudId(),
                student.getUsername(),
                student.getEmail(),
                student.getPassword()
        );
    }

    @Override
    public Student getEntity(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT * FROM Student WHERE username = ?", username);
        if (rst.next()){
            return new Student(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getDate(6).toLocalDate()
            );
        }
        return null;
    }

    @Override
    public boolean fetchExistingUsername(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT username FROM Student WHERE username = ?", username);
        return rst.next();
    }

    @Override
    public boolean fetchExistingEmail(String email) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT email FROM Student WHERE email = ?", email);
        return rst.next();
    }

    @Override
    public Student fetchStudentDetails(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT * FROM Student WHERE username = ?", username);
        while (rst.next()){
            return new Student(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getDate(6).toLocalDate()
            );
        }
        return null;
    }

    @Override
    public String fetchStudentName(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT stud_name FROM Student WHERE username = ?", username);
        while (rst.next()){
            return rst.getString(1);
        }
        return "Student";
    }

    @Override
    public boolean updateEntity(Student student) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Student SET stud_name = ?, username = ?, email = ?, password = ?, date_of_birth = ? WHERE stud_id = ?",
                student.getStudName(),
                student.getUsername(),
                student.getEmail(),
                student.getPassword(),
                student.getDateOfBirth(),
                student.getStudId()
        );

    }

    @Override
    public String getEntityIdByUsername(String username) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT stud_id FROM Student WHERE username = ?", username);
        while (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return false;
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        return "";
    }

    @Override
    public ArrayList<Student> getAll() throws SQLException {
        return null;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        return "";
    }
}
