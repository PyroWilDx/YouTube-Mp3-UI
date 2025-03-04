package pyrowildx.youtube.mp3.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainController {
    private static final int DEFAULT_IMAGE_WIDTH = 3600;
    private static final String DEFAULT_IMAGE_FORMAT = "JPEG";

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

    @FXML
    private void initialize() {
        setupVideoListView();
    }

    private void setupVideoListView() {
        videoListView.setCellFactory(_ -> new ListCell<>() {
            Video prevVideo = null;

            @Override
            protected void updateItem(Video item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (item.equals(prevVideo)) {
                    return;
                }
                prevVideo = item;

                HBox hBox = new HBox(8);
                int hBoxHeight = 116;
                hBox.setMinHeight(hBoxHeight);
                hBox.setPrefHeight(hBoxHeight);
                hBox.setMaxHeight(hBoxHeight);
                hBox.setAlignment(Pos.CENTER_LEFT);

                VBox vImageContainer = new VBox(8);
                int vImageContainerWidth = 192;
                int vImageContainerHeight = hBoxHeight - 8;
                vImageContainer.setMinWidth(vImageContainerWidth);
                vImageContainer.setPrefWidth(vImageContainerWidth);
                vImageContainer.setMaxWidth(vImageContainerWidth);
                vImageContainer.setMinHeight(vImageContainerHeight);
                vImageContainer.setPrefHeight(vImageContainerHeight);
                vImageContainer.setMaxHeight(vImageContainerHeight);
                vImageContainer.setAlignment(Pos.CENTER);

                ImageView vImageView = new ImageView();
                if (item.vImageURL != null && !item.vImageURL.isEmpty()) {
                    vImageView.setImage(new Image(item.vImageURL, true));
                }
                vImageView.setFitWidth(vImageContainerWidth);
                vImageView.setFitHeight(vImageContainerHeight);
                vImageView.setPreserveRatio(true);

                vImageContainer.getChildren().add(vImageView);

                TextField vTitleField = new TextField(item.vTitle);
                vTitleField.textProperty().addListener((_, _, newValue)
                        -> item.vTitle = newValue);

                TextField vImageURLField = new TextField(item.vImageURL);
                vImageURLField.textProperty().addListener((_, _, newValue)
                        -> {
                    item.vImageURL = newValue;
                    vImageView.setImage(new Image(item.vImageURL, true));
                });

                TextField vImageWidthField = new TextField(String.valueOf(item.vImageWidth));
                vImageWidthField.setTextFormatter(new TextFormatter<>(change -> {
                    if (change.getControlNewText().matches("\\d*")) {
                        return change;
                    }
                    return null;
                }));
                vImageWidthField.textProperty().addListener((_, _, newValue)
                        -> item.vImageWidth = Integer.parseInt(newValue));

                ComboBox<String> vImageFormatComboBox = new ComboBox<>();
                vImageFormatComboBox.getItems().addAll("JPEG", "PNG");
                vImageFormatComboBox.setValue(item.vImageFormat);
                vImageFormatComboBox.valueProperty().addListener((_, _, newValue)
                        -> item.vImageFormat = newValue);

                Button rmButton = new Button("Remove");
                rmButton.setOnAction(_ -> {
                    videoListView.getItems().remove(item);
                    if (videoListView.getItems().isEmpty()) {
                        emptyLabel.setVisible(true);
                    }
                });

                hBox.getChildren().addAll(vImageContainer, vTitleField, vImageURLField, vImageWidthField, vImageFormatComboBox, rmButton);
                setGraphic(hBox);
            }
        });
    }

    @FXML
    private void onPlusButtonAction(ActionEvent e) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.setResizable(false);

        Label infoLabel = new Label("Enter URL");
        TextField vURLTextField = new TextField();

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(_ -> popupStage.close());

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(_ -> {
            Video newVideo = makeVideoFromURL(vURLTextField.getText());
            if (newVideo != null) {
                emptyLabel.setVisible(false);
                videoListView.getItems().add(newVideo);
                popupStage.close();
            }
        });

        HBox buttonLayout = new HBox(8, cancelButton, confirmButton);
        buttonLayout.setStyle("-fx-alignment: center;");

        VBox popupLayout = new VBox(8, infoLabel, vURLTextField, buttonLayout);
        popupLayout.setStyle("-fx-padding: 16px; -fx-alignment: center;");

        Scene popupScene = new Scene(popupLayout, 300, 160);
        popupScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelButton.fire();
            }
        });

        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private Video makeVideoFromURL(String vURL) {
        if (isYouTubeURL(vURL)) {
            return makeYouTubeVideoFromURL(vURL);
        } else {
            if (isAudioFile(vURL)) {
                String vTitle = vURL.substring(vURL.lastIndexOf("/") + 1);
                return new Video(vURL, vTitle, "", DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_FORMAT);
            }
        }
        return null;
    }

    private boolean isYouTubeURL(String vURL) {
        return vURL.contains("youtube.com/") || vURL.contains("youtu.be/");
    }

    private Video makeYouTubeVideoFromURL(String vURL) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bin/yt-dlp.exe", "--quiet", "--ffmpeg-location", "bin/YouTube-Mp3/bin/ffmpeg.exe", "--dump-json", "--no-playlist", "--skip-download", vURL);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder jsonOutput = new StringBuilder();
            String currLine;
            while ((currLine = bufferedReader.readLine()) != null) {
                jsonOutput.append(currLine);
            }

            process.waitFor();

            if (!jsonOutput.isEmpty()) {
                JSONObject jsonObject = new JSONObject(jsonOutput.toString());
                String vTitle = jsonObject.getString("title");
                String vImageURL = String.format("https://img.youtube.com/vi/%s/mqdefault.jpg", jsonObject.getString("id"));
                return new Video(vURL, vTitle, vImageURL, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_FORMAT);
            }
        } catch (IOException | InterruptedException | JSONException e) {
            return null;
        }
        return null;
    }

    private boolean isAudioFile(String vURL) {
        String vURLLower = vURL.toLowerCase();
        return vURLLower.endsWith(".mp3")
                || vURLLower.endsWith(".wav")
                || vURLLower.endsWith(".flac")
                || vURLLower.endsWith(".ogg")
                || vURLLower.endsWith(".m4a")
                || vURLLower.endsWith(".aac");
    }

    private static class Video {
        public String vURL;
        public String vTitle;
        public String vImageURL;
        public int vImageWidth;
        public String vImageFormat;

        public Video(String vURL_, String vTitle_, String vImageURL_, int vImageWidth_, String vImageFormat_) {
            vURL = vURL_;
            vTitle = vTitle_;
            vImageURL = vImageURL_;
            vImageWidth = vImageWidth_;
            vImageFormat = vImageFormat_;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Video v)) {
                return false;
            }

            return vURL.equals(v.vURL)
                    && vTitle.equals(v.vTitle)
                    && vImageURL.equals(v.vImageURL)
                    && vImageWidth == v.vImageWidth
                    && vImageFormat.equals(v.vImageFormat);
        }
    }
}
