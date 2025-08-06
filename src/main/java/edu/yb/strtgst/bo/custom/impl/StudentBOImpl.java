package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.dao.custom.StudentDAO;
import edu.yb.strtgst.dao.custom.impl.StudentDAOImpl;
import edu.yb.strtgst.dto.StudentDto;
import edu.yb.strtgst.entity.Student;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentBOImpl implements StudentBO {
    StudentDAO studentDAO = new StudentDAOImpl();

    public boolean addStudent(StudentDto studentDto) throws SQLException {
        return studentDAO.addEntity(new Student(studentDto.getStudId(), studentDto.getUsername(), studentDto.getEmail(), studentDto.getPassword()));
    }

    public StudentDto getStudent(String username) throws SQLException {
        Student student = studentDAO.getEntity(username);
        return new StudentDto(
                student.getStudId(),
                student.getUsername(),
                student.getEmail(),
                student.getPassword()
        );
    }

    public boolean fetchExistingUsername(String username) throws SQLException{
        return studentDAO.fetchExistingUsername(username);
    }

    public boolean fetchExistingEmail(String email) throws SQLException{
        return studentDAO.fetchExistingEmail(email);
    }

    public StudentDto fetchStudentDetails(String username) throws SQLException{
        Student student = studentDAO.getEntity(username);
        return new StudentDto(
                student.getStudId(),
                student.getUsername(),
                student.getEmail(),
                student.getPassword(),
                student.getStudName(),
                student.getDateOfBirth()
        );
    }

    public String fetchStudentName(String username) throws SQLException{
       return studentDAO.fetchStudentName(username);
    }

    public boolean updateStudent(StudentDto studentDto) throws SQLException {
        return studentDAO.updateEntity(new Student(studentDto.getStudId(), studentDto.getUsername(), studentDto.getEmail(), studentDto.getPassword()));

    }

    public String getStudentIdByUsername(String username) throws SQLException {
        return studentDAO.getEntityIdByUsername(username);
    }
}
