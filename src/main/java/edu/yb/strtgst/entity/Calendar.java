package edu.yb.strtgst.entity;

import java.io.Serializable;

public class Calendar implements Serializable {
    private String id;
    private String title;
    private String location;
    private Boolean isFullDay;
    private String fromDateTime;
    private String toDateTime;
    private String repeatType;
}
