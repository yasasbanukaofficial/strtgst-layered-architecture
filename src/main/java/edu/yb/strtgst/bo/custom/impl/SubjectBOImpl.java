package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.dao.custom.impl.SubjectDAOImpl;
import edu.yb.strtgst.db.DBConnection;
import edu.yb.strtgst.dto.SubjectDto;
import edu.yb.strtgst.entity.Subject;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SubjectBOImpl implements SubjectBO {
    SubjectDAOImpl subjectDAO = new SubjectDAOImpl();
    @Override
    public boolean saveSubject(SubjectDto subjectDto) throws SQLException {
        return subjectDAO.addEntity(new Subject(subjectDto.getSubId(), subjectDto.getSubId(), subjectDto.getSubName(), subjectDto.getSubDescription(), subjectDto.getTotalMarks()));
    }

    @Override
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
                boolean gradeMarksUpdate = subjectDAO.addGradeMarks(subId, subjectDto.getTotalMarks());
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

    @Override
    public boolean addGradeMarks(String subId, String newMarks) throws SQLException {
        return subjectDAO.addGradeMarks(subId, newMarks);
    }

    @Override
    public boolean updateGradeMarks(String subId, String subjectMarks) throws SQLException {
        return subjectDAO.updateGradeMarks(subId, subjectMarks);
    }

    @Override
    public boolean deleteGrade(String subId) throws SQLException {
        return subjectDAO.deleteGrade(subId);
    }

    @Override
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
                boolean isGradeMarksUpdated = subjectDAO.updateGradeMarks(subjectDto.getSubId(), subjectDto.getTotalMarks());
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

    @Override
    public boolean deleteSubject(String subjectId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean isSubDeleted = CrudUtil.execute("DELETE FROM Subject WHERE sub_id = ?", subjectId);
            if (isSubDeleted) {
                boolean isGradeDeleted = subjectDAO.deleteGrade(subjectId);
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

    @Override
    public String fetchExistingID(String subjectName) throws SQLException {
        return subjectDAO.fetchExistingID(subjectName);
    }

    @Override
    public ArrayList<SubjectDto> getAllSubjects() throws SQLException {
        ArrayList <Subject> subjects = subjectDAO.getAll();
        ResultSet rst = CrudUtil.execute("SELECT * FROM Subject");
        ArrayList<SubjectDto> subjectDtos = new ArrayList<>();
        for (Subject subject : subjects) {
            SubjectDto subjectDto = new SubjectDto(
                    subject.getSubId(),
                    subject.getSubId(),
                    subject.getSubName(),
                    subject.getSubDescription(),
                    subject.getTotalMarks()
            );
            subjectDtos.add(subjectDto);
        }
        return subjectDtos;
    }

    @Override
    public String loadNextGradeID(){
        return subjectDAO.loadNextID();
    }

    @Override
    public double getGPACalculation() {
        return subjectDAO.getGPACalculation();
    }

    @Override
    public double convertMarksToGPA(double marks) {
        return subjectDAO.convertMarksToGPA(marks);
    }
}
