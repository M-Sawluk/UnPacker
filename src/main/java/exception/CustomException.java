package exception;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CustomException extends Exception{
    private Pane uploadPane;

    public CustomException(Pane uploadPane) {
        this.uploadPane = uploadPane;
    }

    public CustomException(Pane pane, Exception e) {
        this.uploadPane = pane;
        uploadPane.getChildren().clear();
        Label textField = new Label();
        textField.setPrefHeight(500);
        textField.setPrefWidth(500);
        StackTraceElement[] stackTrace = e.getStackTrace();
        String stack = "";
        for (StackTraceElement stackTraceElement : stackTrace) {
            stack += stackTraceElement + "\n";
        }
        textField.setText(e.getMessage() +"\n"+ stack);
        textField.setId("area" + String.valueOf(546));
        uploadPane.getChildren().add(textField);
    }

}
