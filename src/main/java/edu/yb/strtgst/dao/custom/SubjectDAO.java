package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Subject;

import java.sql.SQLException;

public interface SubjectDAO extends CrudDAO<Subject> {
    double getGPACalculation();
    boolean addGradeMarks(String id, String newMarks) throws SQLException;
    boolean updateGradeMarks(String subId, String subjectMarks) throws SQLException;
    boolean deleteGrade(String subjectId) throws SQLException;
    double convertMarksToGPA(double marks);
}
