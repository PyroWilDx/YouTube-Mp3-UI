package pyrowildx.youtube.mp3.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(MainApplication.class.getResource("main-view.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle("YouTube-Mp3");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
