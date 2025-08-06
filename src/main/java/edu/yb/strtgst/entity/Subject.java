package edu.yb.strtgst.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Subject implements Serializable {
    private String subId;
    private String studId;
    private String subName;
    private String subDescription;
    private String totalMarks;
}
