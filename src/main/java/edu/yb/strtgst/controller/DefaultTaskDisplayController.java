package edu.yb.strtgst.controller;

import edu.yb.strtgst.util.Navigation;
import edu.yb.strtgst.util.View;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class DefaultTaskDisplayController {
    public AnchorPane ancDefaultTask;

    public void addTask(MouseEvent mouseEvent) {
        Navigation.navigateTo(ancDefaultTask, View.ADD_TASK);
    }
}
