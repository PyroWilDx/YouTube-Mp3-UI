package pyrowildx.youtube.mp3.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL mainViewFXML = getClass().getResource("main-view.fxml");
        FXMLLoader loader = new FXMLLoader(mainViewFXML);

        Scene scene = new Scene(loader.load());
        URL mainViewCSS = getClass().getResource("main-view.css");
        if (mainViewCSS == null) {
            System.out.println("MainApplication: File main-view.css not found.");
            return;
        }
        scene.getStylesheets().add(mainViewCSS.toExternalForm());

        stage.setTitle("YouTube-Mp3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
