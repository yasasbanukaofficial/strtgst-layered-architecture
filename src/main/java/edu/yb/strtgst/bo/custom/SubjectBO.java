package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.SubjectDto;

import java.sql.SQLException;
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

    double getGPACalculation();

    String loadNextID(String tableName, String columnName);

    double convertMarksToGPA(double marks);
}
