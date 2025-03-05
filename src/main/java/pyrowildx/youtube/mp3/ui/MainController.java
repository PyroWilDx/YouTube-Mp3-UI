package pyrowildx.youtube.mp3.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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

                Utils.addStyle(this, "-fx-background-color: RGB(42, 42, 42);");
                Utils.addStyle(this, "-fx-border-width: 0 0 2 0; -fx-border-color: RGB(68, 68, 68);");

                HBox hBox = new HBox(8);
                int hBoxHeight = 116;
                Utils.setRegionHeight(hBox, hBoxHeight);
                hBox.setAlignment(Pos.CENTER_LEFT);

                VBox vImageContainer = new VBox(8);
                int vImageContainerWidth = 192;
                int vImageContainerHeight = hBoxHeight - 8;
                Utils.setRegionWidth(vImageContainer, vImageContainerWidth);
                Utils.setRegionHeight(vImageContainer, vImageContainerHeight);
                vImageContainer.setAlignment(Pos.CENTER);

                ImageView vImageView = new ImageView();
                if (item.vImageURL != null && !item.vImageURL.isEmpty()) {
                    vImageView.setImage(new Image(item.vImageURL, true));
                }
                vImageView.setFitWidth(vImageContainerWidth);
                vImageView.setFitHeight(vImageContainerHeight);
                vImageView.setPreserveRatio(true);

                vImageContainer.getChildren().add(vImageView);

                int widgetPrefHeight = hBoxHeight - 32;

                TextArea vTitleField = new TextArea(item.vTitle);
                Utils.setRegionWidth(vTitleField, 260);
                Utils.setRegionHeight(vTitleField, widgetPrefHeight);
                vTitleField.setWrapText(true);
                vTitleField.textProperty().addListener((_, _, newValue)
                        -> item.vTitle = newValue);

                TextArea vImageURLField = new TextArea(item.vImageURL);
                Utils.setRegionWidth(vImageURLField, 160);
                Utils.setRegionHeight(vImageURLField, widgetPrefHeight);
                vImageURLField.setWrapText(true);
                vImageURLField.textProperty().addListener((_, _, newValue)
                        -> {
                    item.vImageURL = newValue;
                    vImageView.setImage(new Image(item.vImageURL, true));
                });

                TextField vImageWidthField = new TextField(String.valueOf(item.vImageWidth));
                Utils.setRegionWidth(vImageWidthField, 80);
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
        Stage mainStage = (Stage) videoListView.getScene().getWindow();
        Rectangle darkOverlay = new Rectangle();
        darkOverlay.setFill(Color.rgb(0, 0, 0, 0.38));
        darkOverlay.setWidth(mainStage.getWidth());
        darkOverlay.setHeight(mainStage.getHeight());
        AnchorPane mainRoot = (AnchorPane) mainStage.getScene().getRoot();
        mainRoot.getChildren().add(darkOverlay);

        Stage popupStage = new Stage();
        popupStage.setWidth(360);
        popupStage.setHeight(200);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.setResizable(false);

        Runnable popupStageCloseFn = () -> {
            mainRoot.getChildren().remove(darkOverlay);
            popupStage.close();
        };

        Label infoLabel = new Label("Enter URL");
        infoLabel.setStyle("-fx-text-fill: " + Const.whiteTextColorRGB + "; " +
                "-fx-font-size: 22px; " +
                "-fx-font-weight: BOLD;");

        TextField vURLTextField = new TextField();
        vURLTextField.setStyle("-fx-text-fill: " + Const.whiteTextColorRGB + "; " +
                "-fx-font-size: 14px; " +
                "-fx-background-color: RGB(40, 41, 42); " +
                "-fx-border-color: RGB(80, 80, 80); " +
                "-fx-border-width: 2");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("SecondaryButton");
        Utils.addStyle(cancelButton, "-fx-font-size: 16px;");
        cancelButton.setOnAction(_ -> popupStageCloseFn.run());

        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("MainButton");
        Utils.addStyle(confirmButton, "-fx-font-size: 16px; -fx-font-weight: BOLD;");
        confirmButton.setOnAction(_ -> {
            Video newVideo = makeVideoFromURL(vURLTextField.getText());
            if (newVideo != null) {
                emptyLabel.setVisible(false);
                videoListView.getItems().add(newVideo);
                popupStageCloseFn.run();
            }
        });

        HBox buttonLayout = new HBox(32, cancelButton, confirmButton);
        buttonLayout.setStyle("-fx-alignment: CENTER;");

        VBox popupLayout = new VBox(32, infoLabel, vURLTextField, buttonLayout);
        popupLayout.setStyle("-fx-background-color: RGB(38, 44, 48); " +
                "-fx-padding: 16px; " +
                "-fx-alignment: CENTER; " +
                "-fx-border-color: RGB(100, 100, 100); " +
                "-fx-border-width: 4;");

        Scene popupScene = new Scene(popupLayout, 300, 160);
        Utils.addStyleSheet(MainApplication.class, popupScene, Const.mainViewCSSFile);
        popupScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelButton.fire();
            }
        });

        Window mainWindow = videoListView.getScene().getWindow();
        popupStage.setX(mainWindow.getX() + (mainWindow.getWidth() - popupStage.getWidth()) / 2);
        popupStage.setY(mainWindow.getY() + (mainWindow.getHeight() - popupStage.getHeight()) / 2);

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
