package edu.yb.strtgst.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

public class Navigation {

    private Navigation() {}

    public static void navigateTo(AnchorPane targetPane, View view){
        try{
            targetPane.getChildren().clear();
            AnchorPane load = FXMLLoader.load(Navigation.class.getResource(view.getPath()));
            load.prefWidthProperty().bind(targetPane.widthProperty());
            load.prefHeightProperty().bind(targetPane.heightProperty());
            targetPane.getChildren().add(load);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Cant Identify the url path to:    " + view.name());
            e.printStackTrace();
        }
    }
}
