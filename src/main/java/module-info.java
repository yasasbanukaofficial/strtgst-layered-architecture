module edu.yb.strtgst {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.yb.strtgst to javafx.fxml;
    exports edu.yb.strtgst;
}