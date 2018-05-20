package menagers;

import com.jfoenix.controls.JFXTimePicker;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class GridPaneManager {
    private static int FILES_AREA_NUMBER = 1;
    private static int TIMES_NUMBER = 1;


    public static void addFileArea(GridPane gridPane, String text) {
        if (FILES_AREA_NUMBER < 5) {
            gridPane.add(getFileTextField(text), 1, FILES_AREA_NUMBER);
        }
        FILES_AREA_NUMBER++;
    }

    public static void subtractTextField(GridPane gridPane) {
        ObservableList<Node> children = gridPane.getChildren();
        if (FILES_AREA_NUMBER > 1) {
            FILES_AREA_NUMBER--;
        }
        String lastTextField = "area" + String.valueOf(FILES_AREA_NUMBER);
        children.removeIf(i -> lastTextField.equals(i.getId()));
    }

    public static void addTime(GridPane gridPane) {
        if (TIMES_NUMBER < 5)
            gridPane.add(getTime(), 1, TIMES_NUMBER);
        TIMES_NUMBER++;
    }

    public static void subtractTime(GridPane gridPane) {
        ObservableList<Node> children = gridPane.getChildren();
        if (TIMES_NUMBER > 1) {
            TIMES_NUMBER--;
        }
        String lastTextField = "time" + String.valueOf(TIMES_NUMBER);
        children.removeIf(i -> lastTextField.equals(i.getId()));
    }

    public static int getFilesAreaNumber() {return FILES_AREA_NUMBER; }

    private static JFXTimePicker getTime() {
        JFXTimePicker jfxTimePicker = new JFXTimePicker();
        jfxTimePicker.setDefaultColor(Color.valueOf("#ff5400"));
        jfxTimePicker.setPrefWidth(149.0);
        jfxTimePicker.setPromptText("Wybierz czas");
        jfxTimePicker.setId("time" + String.valueOf(TIMES_NUMBER));
        return jfxTimePicker;
    }

    private static TextField getFileTextField(String content) {
        TextField textField = new TextField();
        textField.setText(content);
        textField.setId("area" + String.valueOf(FILES_AREA_NUMBER));
        textField.setPrefHeight(29);
        textField.setPromptText("Wpisz nazwę pliku");
        return textField;
    }

    public static void setFilesAreaNumber(int filesAreaNumber) { FILES_AREA_NUMBER = filesAreaNumber;}
}
