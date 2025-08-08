package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Assignment;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AssignmentDAO extends CrudDAO<Assignment> {
    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SUBJECT);

    boolean addGradeMarks(String subId, String assignmentMarks) throws SQLException;

    boolean updateSubMarks(String subId, String assignmentMarks) throws SQLException;

    ArrayList<ArrayList> getAllAssignmentStatus() throws SQLException;

    String getPendingOrOverdueAssignmentCount() throws SQLException;

}
