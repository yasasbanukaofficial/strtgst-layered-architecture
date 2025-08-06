package edu.yb.strtgst.dto.tm;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskTM {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private LocalDate dueDate;
    private String status; // "pending", "completed", or "overdue"
}
