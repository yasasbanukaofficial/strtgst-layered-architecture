package edu.yb.strtgst;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource(View.INTRO.getPath()));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }
}
