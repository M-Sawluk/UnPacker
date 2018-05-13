package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.FileBeat.checkIfProcessIsRunning;
import static utils.FileBeat.startFilebeat;
import static utils.PropertiesUtils.*;

public class FileBeatController{
    private static final String EMPTY_TEXT = "Specify Beats Location";
    private static final String LOCATION_PROP_NAME = "location";
    private static final String LOCATION_LOCK_PROP_NAME = "location_lock";
    private static final String PROCESS_NAME = "filebeat";
    @FXML
    private TextField beatLocation;
    @FXML
    private CheckBox lockLocation;
    @FXML
    private Button dirChooser;
    @FXML
    private Circle indicator;

    @FXML
    void openDirectoryChooser(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(beatLocation.getScene().getWindow());

        if (selectedDirectory == null) {
            beatLocation.setText("No Directory selected");
        } else {
            String fileBeatPath = selectedDirectory.getAbsolutePath();
            beatLocation.setText(fileBeatPath);
            saveProp("location", fileBeatPath);
        }
    }

    @FXML
    public void lockInputs(ActionEvent actionEvent) {
        lockSelectedControls();
    }

    @FXML
    public void startFileBeat(ActionEvent actionEvent) {
//        startFilebeat(beatLocation.getText());
    }

    private void lockSelectedControls() {
        if (lockLocation.isSelected()) {
            saveProp(LOCATION_LOCK_PROP_NAME, Boolean.TRUE.toString());
            beatLocation.setDisable(true);
            dirChooser.setDisable(true);
        } else {
            saveProp(LOCATION_LOCK_PROP_NAME, Boolean.FALSE.toString());
            beatLocation.setDisable(false);
            dirChooser.setDisable(false);
        }
    }



}
