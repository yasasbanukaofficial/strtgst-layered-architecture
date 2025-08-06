module edu.yb.strtgst {
    requires javafx.fxml;
    requires java.sql;
    requires com.calendarfx.view;
    requires static lombok;
    requires java.desktop;
    requires google.genai;
    requires org.checkerframework.checker.qual;
    requires java.mail;
    requires javafx.media;
    requires net.sf.jasperreports.core;


    opens edu.yb.strtgst.controller to javafx.fxml;
    opens edu.yb.strtgst.dto to javafx.base;
    exports edu.yb.strtgst;
}