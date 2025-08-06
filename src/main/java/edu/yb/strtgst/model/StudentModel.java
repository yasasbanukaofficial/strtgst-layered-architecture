package edu.yb.strtgst.model;

import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentModel {
    public boolean addStudent(StudentDto studentDto) throws SQLException{
        return CrudUtil.execute(
                "INSERT INTO Student (stud_id, username, email, password) VALUES (?,?,?,?)",
                studentDto.getStudId(),
                studentDto.getUsername(),
                studentDto.getEmail(),
                studentDto.getPassword()
        );
    }

    public static StudentDto getStudent(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT * FROM Student WHERE username = ?", username);
        if (rst.next()){
             return new StudentDto(
                     rst.getString(1),
                     rst.getString(3),
                     rst.getString(4),
                     rst.getString(5)
             );
        }
        return null;
    }

    public boolean fetchExistingUsername(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT username FROM Student WHERE username = ?", username);
        return rst.next();
    }

    public boolean fetchExistingEmail(String email) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT email FROM Student WHERE email = ?", email);
        return rst.next();
    }

    public StudentDto fetchStudentDetails(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT * FROM Student WHERE username = ?", username);
        while (rst.next()){
            return new StudentDto(
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

    public String fetchStudentName(String username) throws SQLException{
        ResultSet rst = CrudUtil.execute("SELECT stud_name FROM Student WHERE username = ?", username);
        while (rst.next()){
            return rst.getString(1);
        }
        return "Student";
    }

    public boolean updateStudent(StudentDto studentDto) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Student SET stud_name = ?, username = ?, email = ?, password = ?, date_of_birth = ? WHERE stud_id = ?",
                studentDto.getStudName(),
                studentDto.getUsername(),
                studentDto.getEmail(),
                studentDto.getPassword(),
                studentDto.getDateOfBirth(),
                studentDto.getStudId()
        );

    }

    public static String getStudentIdByUsername(String username) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT stud_id FROM Student WHERE username = ?", username);
        while (rst.next()){
            return rst.getString(1);
        }
        return null;
    }
}
