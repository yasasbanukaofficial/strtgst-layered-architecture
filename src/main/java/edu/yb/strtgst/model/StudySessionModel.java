package edu.yb.strtgst.model;

import edu.yb.strtgst.dto.StudySessionDto;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class StudySessionModel {

    public boolean addStudySession(StudySessionDto studySessionDto) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO study_session VALUES (?, ?, ?, ?, ?, ?, ?)",
                studySessionDto.getSsId(),
                studySessionDto.getSsName(),
                studySessionDto.getLocation() != null ? studySessionDto.getLocation() : "",
                studySessionDto.isFullDay(),
                studySessionDto.getStartTime() != null ? studySessionDto.getStartTime() : 
                    new Timestamp(studySessionDto.getDate().getTime()),
                studySessionDto.getEndTime(),
                studySessionDto.getRecurrenceRule() != null ? studySessionDto.getRecurrenceRule() : ""
        );
    }

    public boolean updateStudySession(StudySessionDto studySessionDto) throws SQLException {
        return CrudUtil.execute(
                "UPDATE study_session SET ss_name = ?, location = ?, full_day = ?, from_date = ?, to_date = ?, repeat_type = ? WHERE ss_id = ?",
                studySessionDto.getSsName(),
                studySessionDto.getLocation() != null ? studySessionDto.getLocation() : "",
                studySessionDto.isFullDay(),
                studySessionDto.getStartTime() != null ? studySessionDto.getStartTime() : 
                    new Timestamp(studySessionDto.getDate().getTime()),
                studySessionDto.getEndTime(),
                studySessionDto.getRecurrenceRule() != null ? studySessionDto.getRecurrenceRule() : "",
                studySessionDto.getSsId()
        );
    }

    public boolean deleteStudySession(int ssId) throws SQLException {
        return CrudUtil.execute("DELETE FROM study_session WHERE ss_id = ?", ssId);
    }

    public ArrayList<StudySessionDto> getAllStudySessions() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM study_session");

        ArrayList<StudySessionDto> studySessionDtos = new ArrayList<>();
        while (rst.next()) {
            StudySessionDto studySessionDto = new StudySessionDto(
                    rst.getInt("ss_id"),
                    rst.getString("ss_name"),
                    rst.getString("location"),
                    rst.getBoolean("full_day"),
                    rst.getDate("from_date"),
                    rst.getTimestamp("from_date"),
                    rst.getTimestamp("to_date"),
                    rst.getString("repeat_type")
            );
            studySessionDtos.add(studySessionDto);
        }

        return studySessionDtos;
    }

    public static int getNextStudySessionId() {
        try {
            ResultSet rst = CrudUtil.execute("SELECT MAX(ss_id) AS max_id FROM study_session");
            if (rst.next()) {
                return rst.getInt("max_id") + 1;
            }
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
