package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.LectureDto;

import java.sql.SQLException;
import java.util.ArrayList;

public interface LectureBO extends SuperBO {
    boolean addLecture(LectureDto lectureDto) throws SQLException;
    boolean editLecture(LectureDto lectureDto) throws SQLException;
    boolean deleteLecture(String lectureId) throws SQLException;
    ArrayList<LectureDto> getAllLectures() throws SQLException;
    ArrayList<ArrayList> getAllLectureStatus() throws SQLException;
    boolean updateLectureStatus(String lectureId, String newStatus) throws SQLException;
    String getPendingOrOverdueLectureCount() throws SQLException;
    String loadNextID(String tableName, String columnName);
}
