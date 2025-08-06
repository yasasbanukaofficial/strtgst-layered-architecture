package edu.yb.strtgst.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentScoresDto {
    private int scoreId;
    private String subId;
    private int gradeId;
    private Timestamp updateAt;
}
