package edu.yb.strtgst.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class AcademicDto {
    private String id;
    private String title;
    private String location;
    private Boolean isFullDay;
    private Timestamp fromDateTime;
    private Timestamp toDateTime;
    private String repeatType;
}
