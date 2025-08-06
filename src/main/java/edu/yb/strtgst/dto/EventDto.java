package edu.yb.strtgst.dto;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class EventDto {
    private int eventId;
    private int studId;
    private String eventName;
    private String eventDescription;
    private Date date;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status; // "upcoming", "ongoing", or "ended"
}
