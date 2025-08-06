package edu.yb.strtgst.model;

import edu.yb.strtgst.db.DBConnection;
import edu.yb.strtgst.dto.AssignmentDto;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssignmentModel {
    private final SubjectModel subjectModel = new SubjectModel();
    private boolean saveAssignment(AssignmentDto assignmentDto) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Assignment VALUES (?, ?, ?, ?, ?, ?, ?)",
                assignmentDto.getAssignmentId(),
                assignmentDto.getAssignmentName(),
                assignmentDto.getAssignmentDescription(),
                assignmentDto.getAssignmentMarks(),
                assignmentDto.getSubName(),
                assignmentDto.getDueDate(),
                assignmentDto.getAssignmentStatus()
        );
    }

    public boolean addAssignment(AssignmentDto assignmentDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            boolean isSaved = saveAssignment(assignmentDto);
            if (isSaved){
                String subId = fetchExistingID(assignmentDto.getSubName());
                if (subId == null){
                    AlertUtil.setErrorAlert("Assignment not saved");
                    connection.rollback();
                    return false;
                }

                if ("Completed".equals(assignmentDto.getAssignmentStatus())) {
                    boolean subMarksUpdated = updateSubMarks(subId, assignmentDto.getAssignmentMarks());
                    if (subMarksUpdated){
                        boolean gradeMarksUpdate = addGradeMarks(subId, assignmentDto.getAssignmentMarks());
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

    private boolean addGradeMarks(String subId, String assignmentMarks) throws SQLException {
        return subjectModel.addGradeMarks(subId, assignmentMarks);
    }

    private boolean updateSubMarks(String subId, String assignmentMarks) throws SQLException {
        return subjectModel.updateGradeMarks(subId, assignmentMarks);
    }

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

    public static String fetchExistingID(String subjectName) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Subject WHERE sub_name = ?", subjectName);
        return rst.next() ? rst.getString(1) : null;
    }

    public ArrayList<AssignmentDto> getAllAssignments() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Assignment");

        ArrayList<AssignmentDto> assignmentDtos = new ArrayList<>();
        while (rst.next()) {
            AssignmentDto assignmentDto = new AssignmentDto(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getDate(6).toLocalDate(),
                    rst.getString(7)
            );
            assignmentDtos.add(assignmentDto);
        }

        return assignmentDtos;
    }

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

            String oldSubId = fetchExistingID(oldSubName);
            String newSubId = fetchExistingID(assignmentDto.getSubName());

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
                        boolean gradeMarksUpdate = addGradeMarks(newSubId, assignmentDto.getAssignmentMarks());
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

                    addGradeMarks(oldSubId, assignmentDto.getAssignmentMarks());
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

    public ArrayList<ArrayList> getAllAssignmentStatus() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT assignment_status, due_date, assignment_id FROM Assignment");
        ArrayList<ArrayList> list = new ArrayList<>();

        while (rst.next()) {
            ArrayList<String> row = new ArrayList<>();
            row.add(rst.getString("assignment_status"));
            row.add(rst.getString("due_date"));
            row.add(rst.getString("assignment_id"));
            list.add(row);
        }
        return list;
    }


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

    public String getPendingOrOverdueAssignmentCount() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM Assignment WHERE assignment_status = 'Pending' OR assignment_status = 'Overdue'");
        while (rst.next()){
            return rst.getString(1);
        }
        return "0";
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

    public static String loadNextSubjectID(){
        try {
            return IdLoader.getNextIdForTwoChar("Subject", "sub_id");
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Grade ID");
            e.printStackTrace();
        }
        return "SU001";
    }
}
