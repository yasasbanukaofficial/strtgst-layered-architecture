package edu.yb.strtgst.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Assignment implements Serializable {
    private String assignmentId;
    private String assignmentName;
    private String assignmentDescription;
    private String assignmentMarks;
    private String subName;
    private LocalDate dueDate;
    private String assignmentStatus;
}

