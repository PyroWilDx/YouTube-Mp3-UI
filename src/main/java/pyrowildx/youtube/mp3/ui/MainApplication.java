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
        URL mainViewFXML = MainApplication.class.getResource("main-view.fxml");
        FXMLLoader loader = new FXMLLoader(mainViewFXML);

        Scene scene = new Scene(loader.load());
        Utils.addStyleSheet(MainApplication.class, scene, Const.mainViewCSSFile);

        stage.setTitle("YouTube-Mp3");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setWidth(960);
        stage.setHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
