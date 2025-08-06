package edu.yb.strtgst.dto;

import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AssignmentDto {
    private String assignmentId;
    private String assignmentName;
    private String assignmentDescription;
    private String assignmentMarks;
    private String subName;
    private LocalDate dueDate;
    private String assignmentStatus;
}

