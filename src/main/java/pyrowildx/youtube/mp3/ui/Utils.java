package pyrowildx.youtube.mp3.ui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;

import java.net.URL;

public class Utils {
    private Utils() {
    }

    public static void setRegionWidth(Region r, int w) {
        r.setMinWidth(w);
        r.setPrefWidth(w);
        r.setMaxWidth(w);
    }

    public static void setRegionHeight(Region r, int h) {
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
    }

    public static void addStyle(Node n, String style) {
        n.setStyle(n.getStyle() + style);
    }

    public static void addStyleSheet(Class<?> c, Scene s, String CSSFile) {
        URL CSS = c.getResource(CSSFile);
        if (CSS == null) {
            System.out.printf("File %s not found.%n", CSSFile);
            return;
        }
        s.getStylesheets().add(CSS.toExternalForm());
    }

    public static TextFormatter<String> getDigitTextFormatter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
    }
}
