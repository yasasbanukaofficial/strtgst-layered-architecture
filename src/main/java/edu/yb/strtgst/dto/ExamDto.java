package edu.yb.strtgst.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ExamDto {
    private int examId;
    private String examTitle;
    private String examLocation;
    private Boolean isFullDay;
    private Timestamp fromDateTime;
    private Timestamp toDateTime;
    private String repeatType;
}
