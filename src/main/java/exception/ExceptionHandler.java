package exception;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Pane uploadPane;

    public ExceptionHandler(Pane uploadPane) {
        this.uploadPane = uploadPane;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        uploadPane.getChildren().clear();
        TextField textField = new TextField();
        textField.setText("Exception: "+e.getMessage());
        textField.setText("\nCaused: "+e.getCause());
        textField.setId("area" + String.valueOf(546));
        uploadPane.getChildren().add(textField);
    }
}
