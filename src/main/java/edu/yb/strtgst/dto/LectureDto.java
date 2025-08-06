package edu.yb.strtgst.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LectureDto {
    private String lecId;
    private String subId;
    private String lectureName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}
