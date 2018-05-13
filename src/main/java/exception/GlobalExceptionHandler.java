package exception;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Pane uploadPane;

    public GlobalExceptionHandler(Pane uploadPane) {
        this.uploadPane = uploadPane;
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        uploadPane.getChildren().clear();
        Label textField = new Label();
        textField.setPrefHeight(500);
        textField.setPrefWidth(500);
        Throwable cause = e.getCause().getCause();
        StackTraceElement[] stackTrace = cause.getStackTrace();
        String stack = "";
        for (StackTraceElement stackTraceElement : stackTrace) {
            stack += stackTraceElement + "\n";
        }
        textField.setText(cause.getMessage() +"\n"+ stack);
        textField.setId("area" + String.valueOf(546));
        uploadPane.getChildren().add(textField);
    }
}
