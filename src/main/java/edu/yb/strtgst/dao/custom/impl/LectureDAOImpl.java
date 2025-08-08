package edu.yb.strtgst.dao.custom.impl;

import edu.yb.strtgst.dao.custom.LectureDAO;
import edu.yb.strtgst.entity.Lecture;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class LectureDAOImpl implements LectureDAO {
    @Override
    public boolean addEntity(Lecture lecture) throws SQLException {
        return CrudUtil.execute(
            "INSERT INTO Lecture VALUES (?, ?, ?, ?, ?, ?, ?)",
            lecture.getLecId(),
            lecture.getSubId(),
            lecture.getLectureName(),
            lecture.getDate(),
            lecture.getStartTime(),
            lecture.getEndTime(),
            lecture.getStatus()
        );
    }

    @Override
    public Lecture getEntity(String id) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Lecture WHERE lec_id = ?", id);
        if (rst.next()) {
            return new Lecture(
                rst.getString(1),
                rst.getString(2),
                rst.getString(3),
                rst.getDate(4).toLocalDate(),
                rst.getTime(5).toLocalTime(),
                rst.getTime(6).toLocalTime(),
                rst.getString(7)
            );
        }
        return null;
    }

    @Override
    public boolean updateEntity(Lecture lecture) throws SQLException {
        return CrudUtil.execute(
            "UPDATE Lecture SET sub_id = ?, lecture_name = ?, date = ?, start_time = ?, end_time = ?, status = ? WHERE lec_id = ?",
            lecture.getSubId(),
            lecture.getLectureName(),
            lecture.getDate(),
            lecture.getStartTime(),
            lecture.getEndTime(),
            lecture.getStatus(),
            lecture.getLecId()
        );
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return CrudUtil.execute("DELETE FROM Lecture WHERE lec_id = ?", id);
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Lecture WHERE lecture_name = ?", name);
        return rst.next() ? rst.getString(1) : null;
    }

    @Override
    public ArrayList<Lecture> getAll() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Lecture");
        ArrayList<Lecture> lectures = new ArrayList<>();
        while (rst.next()) {
            lectures.add(new Lecture(
                rst.getString(1),
                rst.getString(2),
                rst.getString(3),
                rst.getDate(4).toLocalDate(),
                rst.getTime(5).toLocalTime(),
                rst.getTime(6).toLocalTime(),
                rst.getString(7)
            ));
        }
        return lectures;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        try {
            return IdLoader.getNextID(tableName, columnName);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading a Lecture ID");
            e.printStackTrace();
        }
        return "L001";
    }

    @Override
    public ArrayList<ArrayList> getAllLectureStatus() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT status, date, lec_id FROM Lecture");
        ArrayList<ArrayList> list = new ArrayList<>();
        while (rst.next()) {
            ArrayList<String> row = new ArrayList<>();
            row.add(rst.getString("status"));
            row.add(rst.getString("date"));
            row.add(rst.getString("lec_id"));
            list.add(row);
        }
        return list;
    }

    @Override
    public boolean updateLectureStatus(String lectureId, String newStatus) throws SQLException {
        return CrudUtil.execute(
            "UPDATE Lecture SET status = ? WHERE lec_id = ?",
            newStatus, lectureId
        );
    }

    @Override
    public String getPendingOrOverdueLectureCount() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM Lecture WHERE status = 'Pending' OR status = 'Overdue'");
        if (rst.next()) {
            return rst.getString(1);
        }
        return "0";
    }
}
