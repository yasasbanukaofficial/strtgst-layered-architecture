package edu.yb.strtgst.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Lecture implements Serializable {
    private String lecId;
    private String subId;
    private String lectureName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}
