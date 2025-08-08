package edu.yb.strtgst.entity;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Academic implements Serializable {
    private String id;
    private String title;
    private String location;
    private Boolean isFullDay;
    private Timestamp fromDateTime;
    private Timestamp toDateTime;
    private String repeatType;
}
