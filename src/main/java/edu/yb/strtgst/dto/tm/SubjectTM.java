package edu.yb.strtgst.dto.tm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SubjectTM {
    private String subId;
    private String studId;
    private String subName;
    private String subDescription;
    private String totalMarks;
    private String grade;
}
