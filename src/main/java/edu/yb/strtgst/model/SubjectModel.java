package edu.yb.strtgst.model;

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

public class SubjectModel {
    private boolean saveSubject(SubjectDto subjectDto) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Subject VALUES (?, ?, ?, ?, ?)",
                subjectDto.getSubId(),
                subjectDto.getStudId(),
                subjectDto.getSubName(),
                subjectDto.getSubDescription(),
                subjectDto.getTotalMarks()
        );
    }

    public boolean addSubject(SubjectDto subjectDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean isSaved = saveSubject(subjectDto);
            if (isSaved){
                String subId = fetchExistingID(subjectDto.getSubName());
                if (subId == null){
                    AlertUtil.setErrorAlert("Subject not saved");
                }
                boolean gradeMarksUpdate = addGradeMarks(subId, subjectDto.getTotalMarks());
                if (gradeMarksUpdate){
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e){
            AlertUtil.setErrorAlert("Error when adding an Subject");
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
            connection.close();
            return true;
        }
    }

    public boolean addGradeMarks(String subId, String newMarks) throws SQLException {
        int marks = Integer.parseInt(newMarks);
        String grade = (marks >= 75) ? "A" : (marks >= 65) ? "B" : (marks >= 55) ? "C" : (marks >= 45) ? "D" : "F";
        ResultSet rst = CrudUtil.execute("SELECT * FROM GRADE WHERE sub_id = ?", subId);
        if (rst.next()){
            return CrudUtil.execute("UPDATE GRADE SET marks = marks + ?, grade = ? WHERE sub_id = ?", newMarks, grade, subId);
        } else {
            String gradeId = loadNextGradeID();
            LocalDateTime currentDateTime = LocalDateTime.now();
            return CrudUtil.execute(
                    "INSERT INTO GRADE VALUES (?, ?, ?, ?, ?)",
                    gradeId,
                    subId,
                    newMarks,
                    grade,
                    currentDateTime
            );
        }
    }

    public boolean updateGradeMarks(String subId, String subjectMarks) throws SQLException {
        int marks = Integer.parseInt(subjectMarks);
        String grade = (marks >= 75) ? "A" : (marks >= 65) ? "B" : (marks >= 55) ? "C" : (marks >= 45) ? "D" : "F";
        LocalDateTime currentDateTime = LocalDateTime.now();
        return CrudUtil.execute("UPDATE Grade SET marks = ?, grade = ?, received_date = ? WHERE sub_id = ?", subjectMarks, grade, currentDateTime, subId);
    }

    public boolean deleteGrade(String subId) throws SQLException {
        return CrudUtil.execute("DELETE FROM Grade WHERE sub_id = ?", subId);
    }

    public boolean editSubject(SubjectDto subjectDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean isSubMarksUpdated = CrudUtil.execute(
                    "UPDATE Subject SET sub_name = ?, description = ?, total_marks = ? WHERE sub_id = ?",
                    subjectDto.getSubName(),
                    subjectDto.getSubDescription(),
                    subjectDto.getTotalMarks(),
                    subjectDto.getSubId()
            );
            if (isSubMarksUpdated){
                boolean isGradeMarksUpdated = updateGradeMarks(subjectDto.getSubId(), subjectDto.getTotalMarks());
                if (isGradeMarksUpdated){
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e){
            AlertUtil.setErrorAlert("Error when editing an Subject");
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
            connection.close();
            return true;
        }
    }

    public boolean deleteSubject(String subjectId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean isSubDeleted = CrudUtil.execute("DELETE FROM Subject WHERE sub_id = ?", subjectId);
            if (isSubDeleted) {
                boolean isGradeDeleted = deleteGrade(subjectId);
                if (isGradeDeleted) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when deleting an Subject");
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
            connection.close();
            return true;
        }

    }

    public static String fetchExistingID(String subjectName) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Subject WHERE sub_name = ?", subjectName);
        return rst.next() ? rst.getString(1) : null;
    }

    public ArrayList<SubjectDto> getAllSubjects() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Subject");
        ArrayList<SubjectDto> subjectDtos = new ArrayList<>();
        while (rst.next()) {
            SubjectDto subjectDto = new SubjectDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5)
            );
            subjectDtos.add(subjectDto);
        }
        return subjectDtos;
    }

    public static String loadNextGradeID(){
        try {
            return IdLoader.getNextID("Grade", "grade_id");
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Grade ID");
            e.printStackTrace();
        }
        return "G001";
    }

    public static Double getGPACalculation() {
        try {
            ResultSet rst = CrudUtil.execute("SELECT marks FROM grade");

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

    private static double convertMarksToGPA(double marks) {
        if (marks >= 75) return 4.00;
        if (marks >= 65) return 3.00;
        if (marks >= 55) return 2.00;
        if (marks >= 45) return 1.00;
        return 0.00;
    }
}
