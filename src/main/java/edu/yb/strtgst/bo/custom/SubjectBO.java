package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.db.DBConnection;
import edu.yb.strtgst.dto.SubjectDto;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface SubjectBO extends SuperBO {
    boolean saveSubject(SubjectDto subjectDto) throws SQLException;

    boolean addSubject(SubjectDto subjectDto) throws SQLException;

    boolean addGradeMarks(String subId, String newMarks) throws SQLException;

    boolean updateGradeMarks(String subId, String subjectMarks) throws SQLException;

    boolean deleteGrade(String subId) throws SQLException;

    boolean editSubject(SubjectDto subjectDto) throws SQLException;

    boolean deleteSubject(String subjectId) throws SQLException;

    String fetchExistingID(String subjectName) throws SQLException;

    ArrayList<SubjectDto> getAllSubjects() throws SQLException;

    String loadNextGradeID();

    double getGPACalculation();

    double convertMarksToGPA(double marks);
}
