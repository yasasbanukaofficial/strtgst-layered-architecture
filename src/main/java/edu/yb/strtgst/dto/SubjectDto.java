package edu.yb.strtgst.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SubjectDto {
    private String subId;
    private String studId;
    private String subName;
    private String subDescription;
    private String totalMarks;
}
