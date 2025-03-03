module pyrowildx.youtubemp3ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens pyrowildx.youtube.mp3.ui to javafx.fxml;
    exports pyrowildx.youtube.mp3.ui;
}