package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.custom.LectureBO;
import edu.yb.strtgst.dao.custom.LectureDAO;
import edu.yb.strtgst.dao.custom.impl.LectureDAOImpl;
import edu.yb.strtgst.db.DBConnection;
import edu.yb.strtgst.dto.LectureDto;
import edu.yb.strtgst.entity.Lecture;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class LectureBOImpl implements LectureBO {
    private final LectureDAO lectureDAO = new LectureDAOImpl();

    @Override
    public boolean addLecture(LectureDto lectureDto) throws SQLException {
        return lectureDAO.addEntity(new Lecture(
            lectureDto.getLecId(),
            lectureDto.getSubId(),
            lectureDto.getLectureName(),
            lectureDto.getDate(),
            lectureDto.getStartTime(),
            lectureDto.getEndTime(),
            lectureDto.getStatus()
        ));
    }

    @Override
    public boolean editLecture(LectureDto lectureDto) throws SQLException {
        return lectureDAO.updateEntity(new Lecture(
            lectureDto.getLecId(),
            lectureDto.getSubId(),
            lectureDto.getLectureName(),
            lectureDto.getDate(),
            lectureDto.getStartTime(),
            lectureDto.getEndTime(),
            lectureDto.getStatus()
        ));
    }

    @Override
    public boolean deleteLecture(String lectureId) throws SQLException {
        return lectureDAO.deleteEntity(lectureId);
    }

    @Override
    public ArrayList<LectureDto> getAllLectures() throws SQLException {
        ArrayList<Lecture> lectures = lectureDAO.getAll();
        ArrayList<LectureDto> dtos = new ArrayList<>();
        for (Lecture l : lectures) {
            dtos.add(new LectureDto(
                l.getLecId(),
                l.getSubId(),
                l.getLectureName(),
                l.getDate(),
                l.getStartTime(),
                l.getEndTime(),
                l.getStatus()
            ));
        }
        return dtos;
    }

    @Override
    public ArrayList<ArrayList> getAllLectureStatus() throws SQLException {
        return lectureDAO.getAllLectureStatus();
    }

    @Override
    public boolean updateLectureStatus(String lectureId, String newStatus) throws SQLException {
        return lectureDAO.updateLectureStatus(lectureId, newStatus);
    }

    @Override
    public String getPendingOrOverdueLectureCount() throws SQLException {
        return lectureDAO.getPendingOrOverdueLectureCount();
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
}
