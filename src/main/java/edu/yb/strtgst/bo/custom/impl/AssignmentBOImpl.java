package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AssignmentBO;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.dao.custom.AssignmentDAO;
import edu.yb.strtgst.dao.custom.impl.AssignmentDAOImpl;
import edu.yb.strtgst.db.DBConnection;
import edu.yb.strtgst.dto.AssignmentDto;
import edu.yb.strtgst.entity.Assignment;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class AssignmentBOImpl implements AssignmentBO {
    AssignmentDAO assignmentDAO = new AssignmentDAOImpl();
    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOType.SUBJECT);

    @Override
    public boolean addAssignment(AssignmentDto assignmentDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            boolean isSaved = assignmentDAO.addEntity(new Assignment(assignmentDto.getAssignmentId(), assignmentDto.getAssignmentName(), assignmentDto.getAssignmentDescription(), assignmentDto.getAssignmentMarks(), assignmentDto.getSubName(), assignmentDto.getDueDate(), assignmentDto.getAssignmentStatus()));
            if (isSaved){
                String subId = assignmentDAO.fetchExistingID(assignmentDto.getSubName());
                if (subId == null){
                    AlertUtil.setErrorAlert("Assignment not saved");
                    connection.rollback();
                    return false;
                }

                if ("Completed".equals(assignmentDto.getAssignmentStatus())) {
                    boolean subMarksUpdated = subjectBO.updateGradeMarks(subId, assignmentDto.getAssignmentMarks());
                    if (subMarksUpdated){
                        boolean gradeMarksUpdate = subjectBO.addGradeMarks(subId, assignmentDto.getAssignmentMarks());
                        if (gradeMarksUpdate){
                            connection.commit();
                            return true;
                        }
                    }
                    connection.rollback();
                    return false;
                } else {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e){
            AlertUtil.setErrorAlert("Error when adding an assignment");
            e.printStackTrace();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public boolean addGradeMarks(String subId, String assignmentMarks) throws SQLException {
        return subjectBO.addGradeMarks(subId, assignmentMarks);
    }

    @Override
    public boolean updateSubMarks(String subId, String assignmentMarks) throws SQLException {
        return subjectBO.updateGradeMarks(subId, assignmentMarks);
    }

    @Override
    public boolean deleteAssignment(String assignmentId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            ResultSet assignmentRst = CrudUtil.execute("SELECT * FROM Assignment WHERE assignment_id = ?", assignmentId);
            if (!assignmentRst.next()) {
                connection.rollback();
                return false;
            }

            String subName = assignmentRst.getString("sub_name");
            String assignmentMarks = assignmentRst.getString("assignment_marks");

            String subId = fetchExistingID(subName);
            if (subId == null) {
                connection.rollback();
                return false;
            }

            boolean isSubjectUpdated = CrudUtil.execute(
                    "UPDATE Subject SET total_marks = total_marks - ? WHERE sub_id = ?",
                    assignmentMarks, subId
            );

            if (isSubjectUpdated) {
                ResultSet gradeRst = CrudUtil.execute("SELECT marks FROM GRADE WHERE sub_id = ?", subId);
                if (gradeRst.next()) {
                    int currentMarks = gradeRst.getInt("marks");
                    int newMarks = currentMarks - Integer.parseInt(assignmentMarks);
                    newMarks = Math.max(0, newMarks);


                    String grade = (newMarks >= 75) ? "A" : (newMarks >= 65) ? "B" :
                            (newMarks >= 55) ? "C" : (newMarks >= 45) ? "D" : "F";

                    boolean isGradeUpdated = CrudUtil.execute(
                            "UPDATE Grade SET marks = ?, grade = ? WHERE sub_id = ?",
                            newMarks, grade, subId
                    );

                    if (isGradeUpdated) {
                        boolean isAssignmentDeleted = CrudUtil.execute(
                                "DELETE FROM Assignment WHERE assignment_id = ?",
                                assignmentId
                        );

                        if (isAssignmentDeleted) {
                            connection.commit();
                            return true;
                        }
                    }
                }
            }

            connection.rollback();
            return false;
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when deleting an assignment");
            e.printStackTrace();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public String fetchExistingID(String subjectName) throws SQLException {
        return assignmentDAO.fetchExistingID(subjectName);
    }

    @Override
    public ArrayList<AssignmentDto> getAllAssignments() throws SQLException {
        ArrayList<Assignment> assignments = assignmentDAO.getAll();
        ArrayList<AssignmentDto> assignmentDtos = new ArrayList<>();
        for (Assignment assignment : assignments) {
            assignmentDtos.add(new AssignmentDto(assignment.getAssignmentId(), assignment.getAssignmentName(), assignment.getAssignmentDescription(), assignment.getAssignmentMarks(), assignment.getSubName(), assignment.getDueDate(), assignment.getAssignmentStatus()));
        }
        return assignmentDtos;
    }

    @Override
    public boolean editAssignment(AssignmentDto assignmentDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            ResultSet existingAssignment = CrudUtil.execute(
                    "SELECT * FROM Assignment WHERE assignment_id = ?",
                    assignmentDto.getAssignmentId()
            );

            if (!existingAssignment.next()) {
                connection.rollback();
                return false;
            }

            String oldSubName = existingAssignment.getString("sub_name");
            String oldMarks = existingAssignment.getString("assignment_marks");
            String oldStatus = existingAssignment.getString("assignment_status");

            boolean isUpdated = CrudUtil.execute(
                    "UPDATE Assignment SET assignment_name = ?, assignment_description = ?, assignment_marks = ?, sub_name = ?, due_date = ?, assignment_status = ? WHERE assignment_id = ?",
                    assignmentDto.getAssignmentName(),
                    assignmentDto.getAssignmentDescription(),
                    assignmentDto.getAssignmentMarks(),
                    assignmentDto.getSubName(),
                    assignmentDto.getDueDate(),
                    assignmentDto.getAssignmentStatus(),
                    assignmentDto.getAssignmentId()
            );

            if (!isUpdated) {
                connection.rollback();
                return false;
            }

            String oldSubId = assignmentDAO.fetchExistingID(oldSubName);
            String newSubId = assignmentDAO.fetchExistingID(assignmentDto.getSubName());

            if (!oldSubName.equals(assignmentDto.getSubName())) {
                if ("Completed".equals(oldStatus)) {
                    CrudUtil.execute(
                            "UPDATE Subject SET total_marks = total_marks - ? WHERE sub_id = ?",
                            oldMarks, oldSubId
                    );

                    ResultSet oldGradeRst = CrudUtil.execute("SELECT marks FROM GRADE WHERE sub_id = ?", oldSubId);
                    if (oldGradeRst.next()) {
                        int currentMarks = oldGradeRst.getInt("marks");
                        int newMarks = currentMarks - Integer.parseInt(oldMarks);
                        newMarks = Math.max(0, newMarks);

                        String grade = (newMarks >= 75) ? "A" : (newMarks >= 65) ? "B" :
                                (newMarks >= 55) ? "C" : (newMarks >= 45) ? "D" : "F";

                        CrudUtil.execute(
                                "UPDATE Grade SET marks = ?, grade = ? WHERE sub_id = ?",
                                newMarks, grade, oldSubId
                        );
                    }
                }

                if ("Completed".equals(assignmentDto.getAssignmentStatus())) {
                    boolean subMarksUpdated = CrudUtil.execute(
                            "UPDATE Subject SET total_marks = total_marks + ? WHERE sub_id = ?",
                            assignmentDto.getAssignmentMarks(), newSubId
                    );

                    if (subMarksUpdated) {
                        boolean gradeMarksUpdate = subjectBO.addGradeMarks(newSubId, assignmentDto.getAssignmentMarks());
                        if (!gradeMarksUpdate) {
                            connection.rollback();
                            return false;
                        }
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            } else {
                if ("Completed".equals(oldStatus) && !"Completed".equals(assignmentDto.getAssignmentStatus())) {
                    CrudUtil.execute(
                            "UPDATE Subject SET total_marks = total_marks - ? WHERE sub_id = ?",
                            oldMarks, oldSubId
                    );

                    ResultSet gradeRst = CrudUtil.execute("SELECT marks FROM GRADE WHERE sub_id = ?", oldSubId);
                    if (gradeRst.next()) {
                        int currentMarks = gradeRst.getInt("marks");
                        int newMarks = currentMarks - Integer.parseInt(oldMarks);
                        newMarks = Math.max(0, newMarks);

                        String grade = (newMarks >= 75) ? "A" : (newMarks >= 65) ? "B" :
                                (newMarks >= 55) ? "C" : (newMarks >= 45) ? "D" : "F";

                        CrudUtil.execute(
                                "UPDATE Grade SET marks = ?, grade = ? WHERE sub_id = ?",
                                newMarks, grade, oldSubId
                        );
                    }
                }
                else if (!"Completed".equals(oldStatus) && "Completed".equals(assignmentDto.getAssignmentStatus())) {
                    CrudUtil.execute(
                            "UPDATE Subject SET total_marks = total_marks + ? WHERE sub_id = ?",
                            assignmentDto.getAssignmentMarks(), oldSubId
                    );

                    subjectBO.addGradeMarks(oldSubId, assignmentDto.getAssignmentMarks());
                }
                else if ("Completed".equals(oldStatus) && "Completed".equals(assignmentDto.getAssignmentStatus())
                        && !oldMarks.equals(assignmentDto.getAssignmentMarks())) {
                    int marksDifference = Integer.parseInt(assignmentDto.getAssignmentMarks()) - Integer.parseInt(oldMarks);

                    CrudUtil.execute(
                            "UPDATE Subject SET total_marks = total_marks + ? WHERE sub_id = ?",
                            String.valueOf(marksDifference), oldSubId
                    );

                    ResultSet gradeRst = CrudUtil.execute("SELECT marks FROM GRADE WHERE sub_id = ?", oldSubId);
                    if (gradeRst.next()) {
                        int currentMarks = gradeRst.getInt("marks");
                        int newMarks = currentMarks + marksDifference;
                        newMarks = Math.max(0, newMarks);

                        String grade = (newMarks >= 75) ? "A" : (newMarks >= 65) ? "B" :
                                (newMarks >= 55) ? "C" : (newMarks >= 45) ? "D" : "F";

                        CrudUtil.execute(
                                "UPDATE Grade SET marks = ?, grade = ? WHERE sub_id = ?",
                                newMarks, grade, oldSubId
                        );
                    }
                }
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when updating assignment");
            e.printStackTrace();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public ArrayList<ArrayList> getAllAssignmentStatus() throws SQLException {
        return assignmentDAO.getAllAssignmentStatus();
    }

    @Override
    public boolean updateAssignmentStatus(String assignmentId, String newStatus) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            ResultSet assignmentRst = CrudUtil.execute(
                    "SELECT * FROM Assignment WHERE assignment_id = ?",
                    assignmentId
            );

            if (!assignmentRst.next()) {
                connection.rollback();
                return false;
            }

            String oldStatus = assignmentRst.getString("assignment_status");
            String assignmentMarks = assignmentRst.getString("assignment_marks");
            String subName = assignmentRst.getString("sub_name");

            if (oldStatus.equals(newStatus)) {
                connection.rollback();
                return true;
            }

            boolean statusUpdated = CrudUtil.execute(
                    "UPDATE Assignment SET assignment_status = ? WHERE assignment_id = ?",
                    newStatus, assignmentId
            );

            if (!statusUpdated) {
                connection.rollback();
                return false;
            }

            String subId = fetchExistingID(subName);
            if (subId == null) {
                connection.rollback();
                return false;
            }

            if (!"Completed".equals(oldStatus) && "Completed".equals(newStatus) && !"Overdue".equals(newStatus)) {
                boolean subjectUpdated = CrudUtil.execute(
                        "UPDATE Subject SET total_marks = total_marks + ? WHERE sub_id = ?",
                        assignmentMarks, subId
                );

                if (subjectUpdated) {
                    boolean gradeUpdated = addGradeMarks(subId, assignmentMarks);
                    if (!gradeUpdated) {
                        connection.rollback();
                        return false;
                    }
                } else {
                    connection.rollback();
                    return false;
                }
            }
            else if ("Completed".equals(oldStatus) && !"Completed".equals(newStatus)) {
                boolean subjectUpdated = CrudUtil.execute(
                        "UPDATE Subject SET total_marks = total_marks - ? WHERE sub_id = ?",
                        assignmentMarks, subId
                );

                if (subjectUpdated) {
                    ResultSet gradeRst = CrudUtil.execute("SELECT marks FROM GRADE WHERE sub_id = ?", subId);
                    if (gradeRst.next()) {
                        int currentMarks = gradeRst.getInt("marks");
                        int newMarks = currentMarks - Integer.parseInt(assignmentMarks);
                        newMarks = Math.max(0, newMarks);

                        String grade = (newMarks >= 75) ? "A" : (newMarks >= 65) ? "B" :
                                (newMarks >= 55) ? "C" : (newMarks >= 45) ? "D" : "F";

                        boolean gradeUpdated = CrudUtil.execute(
                                "UPDATE Grade SET marks = ?, grade = ? WHERE sub_id = ?",
                                newMarks, grade, subId
                        );

                        if (!gradeUpdated) {
                            connection.rollback();
                            return false;
                        }
                    }
                } else {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when updating assignment status");
            e.printStackTrace();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public String getPendingOrOverdueAssignmentCount() throws SQLException {
        return assignmentDAO.getPendingOrOverdueAssignmentCount();
    }

    @Override
    public String loadNextID(String tableName, String columnName){
        try {
            return IdLoader.getNextID(tableName, columnName);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Assignment ID");
            e.printStackTrace();
        }
        return "A001";
    }
}
