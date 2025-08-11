package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.SubjectDAO;
import edu.yb.strtgst.entity.Subject;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.dao.SQLUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SubjectDAOImpl implements SubjectDAO {
    @Override
    public boolean addEntity(Subject subject) throws SQLException {
        return SQLUtil.execute(
                "INSERT INTO Subject VALUES (?, ?, ?, ?, ?)",
                subject.getSubId(),
                subject.getStudId(),
                subject.getSubName(),
                subject.getSubDescription(),
                subject.getTotalMarks()
        );
    }

    @Override
    public boolean addGradeMarks(String subId, String newMarks) throws SQLException {
        int marks = Integer.parseInt(newMarks);
        String grade = (marks >= 75) ? "A" : (marks >= 65) ? "B" : (marks >= 55) ? "C" : (marks >= 45) ? "D" : "F";
        ResultSet rst = SQLUtil.execute("SELECT * FROM GRADE WHERE sub_id = ?", subId);
        if (rst.next()){
            return SQLUtil.execute("UPDATE GRADE SET marks = marks + ?, grade = ? WHERE sub_id = ?", newMarks, grade, subId);
        } else {
            String gradeId = loadNextID("Grade", "grade_id");
            LocalDateTime currentDateTime = LocalDateTime.now();
            return SQLUtil.execute(
                    "INSERT INTO GRADE VALUES (?, ?, ?, ?, ?)",
                    gradeId,
                    subId,
                    newMarks,
                    grade,
                    currentDateTime
            );
        }
    }

    @Override
    public boolean updateGradeMarks(String subId, String subjectMarks) throws SQLException {
        int marks = Integer.parseInt(subjectMarks);
        String grade = (marks >= 75) ? "A" : (marks >= 65) ? "B" : (marks >= 55) ? "C" : (marks >= 45) ? "D" : "F";
        LocalDateTime currentDateTime = LocalDateTime.now();
        return SQLUtil.execute("UPDATE Grade SET marks = ?, grade = ?, received_date = ? WHERE sub_id = ?", subjectMarks, grade, currentDateTime, subId);
    }

    @Override
    public boolean deleteGrade(String subId) throws SQLException {
        return SQLUtil.execute("DELETE FROM Grade WHERE sub_id = ?", subId);
    }

    @Override
    public String fetchExistingID(String subjectName) throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM Subject WHERE sub_name = ?", subjectName);
        return rst.next() ? rst.getString(1) : null;
    }

    @Override
    public ArrayList<Subject> getAll() throws SQLException {
        ResultSet rst = SQLUtil.execute("SELECT * FROM Subject");
        ArrayList<Subject> subjects = new ArrayList<>();
        while (rst.next()) {
            Subject subject = new Subject(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5)
            );
            subjects.add(subject);
        }
        return subjects;
    }

    @Override
    public String loadNextID(String tableName, String columnName){
        try {
            return IdLoader.getNextID(tableName, columnName);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a ID");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public double getGPACalculation() {
        try {
            ResultSet rst = SQLUtil.execute("SELECT marks FROM grade");

            double totalGPA = 0;
            int subjectCount = 0;

            while (rst.next()) {
                double marks = rst.getDouble("marks");
                double gpaPoint = convertMarksToGPA(marks);
                totalGPA += gpaPoint;
                subjectCount++;
            }

            if (subjectCount == 0) return 0.00;

            return totalGPA / subjectCount;

        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error calculating GPA: " + e.getMessage());
            e.printStackTrace();
            return 0.00;
        }
    }

    @Override
    public double convertMarksToGPA(double marks) {
        if (marks >= 75) return 4.00;
        if (marks >= 65) return 3.00;
        if (marks >= 55) return 2.00;
        if (marks >= 45) return 1.00;
        return 0.00;
    }

    @Override
    public Subject getEntity(String name) throws SQLException {
        return null;
    }

    @Override
    public boolean updateEntity(Subject entity) throws SQLException {
        return false;
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return false;
    }
}
