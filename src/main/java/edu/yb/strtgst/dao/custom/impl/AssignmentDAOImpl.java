package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.AssignmentDAO;
import edu.yb.strtgst.entity.Assignment;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssignmentDAOImpl implements AssignmentDAO {

    @Override
    public boolean addEntity(Assignment assignment) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Assignment VALUES (?, ?, ?, ?, ?, ?, ?)",
                assignment.getAssignmentId(),
                assignment.getAssignmentName(),
                assignment.getAssignmentDescription(),
                assignment.getAssignmentMarks(),
                assignment.getSubName(),
                assignment.getDueDate(),
                assignment.getAssignmentStatus()
        );
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
    public String fetchExistingID(String subjectName) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Subject WHERE sub_name = ?", subjectName);
        return rst.next() ? rst.getString(1) : null;
    }

    @Override
    public ArrayList<Assignment> getAll() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Assignment");

        ArrayList<Assignment> assignments = new ArrayList<>();
        while (rst.next()) {
            Assignment assignment = new Assignment(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getDate(6).toLocalDate(),
                    rst.getString(7)
            );
            assignments.add(assignment);
        }

        return assignments;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        return "";
    }

    @Override
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

    @Override
    public String getPendingOrOverdueAssignmentCount() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM Assignment WHERE assignment_status = 'Pending' OR assignment_status = 'Overdue'");
        while (rst.next()){
            return rst.getString(1);
        }
        return "0";
    }

    @Override
    public Assignment getEntity(String name) throws SQLException {
        return null;
    }

    @Override
    public boolean updateEntity(Assignment entity) throws SQLException {
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
