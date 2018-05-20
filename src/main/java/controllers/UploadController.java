package controllers;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import menagers.GridPaneManager;
import model.Time;
import utils.FileBeat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static utils.FileBeat.fileBeatNotifier;
import static utils.FileUtils.startCopying;
import static utils.PropertiesUtils.*;
import static utils.TimeUtils.prepareTimePoints;

public class UploadController implements Initializable {
    private static final String SEPARATOR = ":";
    private static final String USER_PROP_NAME = "user";
    private static final String PROJECT_PROP_NAME = "project";
    private static final String FILES_NUMBER_PROP = "file_number";
    private static final String FILES_NAMES_PROP = "file_names";
    private static final String LOG_LOCATION_PROP = "location";
    private static final String KIBANA_URL_PROP = "kibana_url";
    private String fileNames;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField area0;
    @FXML
    private TextField userName;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ChoiceBox<String> offset;
    @FXML
    private JFXDatePicker date;
    @FXML
    private Label fileBeatStatus;
    @FXML
    private TextField logsLocation;
    @FXML
    private TextField kibanaLocation;
    @FXML
    private GridPane fileGrid;
    @FXML
    private GridPane timeGrid;

    @FXML
    public void upload(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Log files", "*.*"));
        List<File> uploadedFiles = fileChooser.showOpenMultipleDialog(uploadButton.getScene().getWindow());
        Optional<List<Time>> times = gatherTimePoints();
        Optional<List<String>> fileNames = gatherFileNames();
        savePops();
        startFileBeat().start();
        startCopyingThread(times, fileNames, uploadedFiles).start();
        openBrowser(event);
    }

    void openBrowser(ActionEvent actionEvent) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(getClass().getResource("Webview.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) (((Node) actionEvent.getSource()).getScene().getWindow());
        window.setScene(scene);
        window.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            userName.setText(loadProperties(USER_PROP_NAME));
            choiceBox.setValue(loadProperties(PROJECT_PROP_NAME));
            String[] split = loadProperties(FILES_NAMES_PROP).split(SEPARATOR);
            GridPaneManager.setFilesAreaNumber(1);
            if (split.length > 0)
                area0.setText(split[0]);
            for (int i = 0; i < split.length - 1; i++) {
                GridPaneManager.addFileArea(fileGrid, split[i + 1]);
            }
            fileBeatNotifier(fileBeatStatus).start();
            logsLocation.setText(loadProperties(LOG_LOCATION_PROP));
            kibanaLocation.setText(loadProperties(KIBANA_URL_PROP));

    }

    private void savePops() {
        saveProp(FILES_NAMES_PROP, fileNames);
        saveProp(USER_PROP_NAME, userName.getText().toLowerCase());
        saveProp(PROJECT_PROP_NAME, choiceBox.getValue());
        saveProp(FILES_NUMBER_PROP, String.valueOf(GridPaneManager.getFilesAreaNumber() - 1));
        saveProp(KIBANA_URL_PROP, kibanaLocation.getText());
        saveProp(LOG_LOCATION_PROP, logsLocation.getText());
    }

    public void addTextField(ActionEvent actionEvent) {
        GridPaneManager.addFileArea(fileGrid, "");
    }

    public void subtractTextField(ActionEvent actionEvent) {
        GridPaneManager.subtractTextField(fileGrid);
    }

    public void addTime(ActionEvent actionEvent) {
        GridPaneManager.addTime(timeGrid);
    }

    public void subtractTime(ActionEvent actionEvent) {
        GridPaneManager.subtractTime(timeGrid);
    }

    private Optional<List<Time>> gatherTimePoints() {
        LocalDate datee = date.getValue();

        if (datee == null) return Optional.empty();

        List<LocalTime> localTimes = timeGrid.getChildren()
                .stream()
                .filter(i -> i.getId() != null && i.getId().contains("time"))
                .map(i -> ((JFXTimePicker) i).getValue())
                .collect(Collectors.toList());
        List<Time> time = prepareTimePoints(datee, localTimes, offset.getValue());
        return Optional.of(time);
    }

    private Optional<List<String>> gatherFileNames() {
        fileNames = "";
        List<String> files = new ArrayList<>();
        for (Node node : fileGrid.getChildren()) {
            if (node.getId() != null && node.getId().contains("area")) {
                String fileName = ((TextField) node).getText();
                fileNames += fileName + SEPARATOR;
                files.add(fileName);
            }
        }
        if (fileNames.isEmpty())
            return Optional.empty();
        else
            return Optional.of(files);
    }

    private Thread startFileBeat() {
        return new Thread(FileBeat::startFilebeat);
    }

    private Thread startCopyingThread(Optional<List<Time>> times, Optional<List<String>> fileNames, List<File> uploadedFiles) {
        return new Thread(() -> startCopying(uploadedFiles, fileNames, times));
    }

}
