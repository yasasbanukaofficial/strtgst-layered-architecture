package edu.yb.strtgst.dao.custom;

import edu.yb.strtgst.dao.CrudDAO;
import edu.yb.strtgst.entity.Lecture;

import java.sql.SQLException;
import java.util.ArrayList;

public interface LectureDAO extends CrudDAO<Lecture> {
    ArrayList<ArrayList> getAllLectureStatus() throws SQLException;
    boolean updateLectureStatus(String lectureId, String newStatus) throws SQLException;
    String getPendingOrOverdueLectureCount() throws SQLException;
}
