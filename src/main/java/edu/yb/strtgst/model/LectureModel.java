package edu.yb.strtgst.model;

import edu.yb.strtgst.dto.LectureDto;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.SQLException;

public class LectureModel {
    public boolean addLecture(LectureDto lectureDto) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Lecture VALUES (?, ?, ?, ?, ?, ?, ?)",
                lectureDto.getLecId(),
                lectureDto.getSubId(),
                lectureDto.getLectureName(),
                lectureDto.getDate(),
                lectureDto.getStartTime(),
                lectureDto.getEndTime(),
                lectureDto.getStatus()
        );
    }
}
