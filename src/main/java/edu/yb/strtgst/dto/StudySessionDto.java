package edu.yb.strtgst.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudySessionDto {
    private int ssId;
    private String ssName;
    private String location;
    private boolean fullDay;
    private Date date;
    private Timestamp startTime;
    private Timestamp endTime;
    private String recurrenceRule;

    public StudySessionDto(int ssId, String ssName, Date date, Timestamp startTime, Timestamp endTime) {
        this.ssId = ssId;
        this.ssName = ssName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = "";
        this.fullDay = false;
        this.recurrenceRule = "";
    }

    public StudySessionDto(int ssId, String ssName, String location, boolean fullDay, 
                         Date date, Timestamp startTime, Timestamp endTime, String recurrenceRule) {
        this.ssId = ssId;
        this.ssName = ssName;
        this.location = location;
        this.fullDay = fullDay;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurrenceRule = recurrenceRule;
    }
}
