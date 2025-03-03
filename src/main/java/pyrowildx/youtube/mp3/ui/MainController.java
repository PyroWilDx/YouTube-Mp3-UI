package pyrowildx.youtube.mp3.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class MainController {
    @FXML
    private Button plusButton;

    @FXML
    private ListView<Video> videoListView;

    @FXML
    private Label emptyLabel;

    @FXML
    private Button dlButton;

    @FXML
    private Button openOutputFolderButton;

    @FXML
    private Label dlProgressLabel;

    @FXML
    private ProgressBar dlProgressBar;

    static class Video {
        private final String URL;

        public Video(String URL_) {
            URL = URL_;
        }

        public String getURL() {
            return URL;
        }
    }
}
