package edu.yb.strtgst.dto.tm;

import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AssignmentTM {
    private String assignmentId;
    private String assignmentName;
    private String assignmentDescription;
    private String assignmentMarks;
    private String subName;
    private LocalDate assignmentDueDate;
    private String assignmentStatus;

}
