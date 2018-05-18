import exception.GlobalExceptionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("UnPacker");
        Scene value = new Scene(root, 600, 400);
        primaryStage.setScene(value);
        primaryStage.show();
        setGlobalExceptionHandler();
    }
    public static void main(String[] args) { launch(args); }

    private void setGlobalExceptionHandler() {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
    }
}
