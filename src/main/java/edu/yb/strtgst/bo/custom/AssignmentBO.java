package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.AssignmentDto;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AssignmentBO extends SuperBO {

    boolean addAssignment(AssignmentDto assignmentDto) throws SQLException;

    boolean addGradeMarks(String subId, String assignmentMarks) throws SQLException;

    boolean updateSubMarks(String subId, String assignmentMarks) throws SQLException;

    boolean deleteAssignment(String assignmentId) throws SQLException;

    String fetchExistingID(String subjectName) throws SQLException;

    ArrayList<AssignmentDto> getAllAssignments() throws SQLException;

    boolean editAssignment(AssignmentDto assignmentDto) throws SQLException;

    ArrayList<ArrayList> getAllAssignmentStatus() throws SQLException;

    boolean updateAssignmentStatus(String assignmentId, String newStatus) throws SQLException;

    String getPendingOrOverdueAssignmentCount() throws SQLException;

    public String loadNextID(String tableName, String columnName);

}
