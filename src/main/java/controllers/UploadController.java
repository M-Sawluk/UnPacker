package controllers;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import model.Time;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static utils.FileUtils.startCopying;
import static utils.PropertiesUtils.*;
import static utils.TimeUtils.prepareTimePoints;

public class UploadController implements Initializable {
    private static final String FLOOR = "_";
    private static final String USER_PROP_NAME = "user";
    private static final String PROJECT_PROP_NAME = "project";
    private static final String PROJECT_PROP_LOCK = "project_lock";
    private static final String FILES_NUMBER_PROP = "file_number";
    private static final String FILES_NAMES_PROP = "file_names";
    private static final String USER_EMPTY = "Enter name";
    private int FILE_AREAS_COUNT = 1;
    private int TIME_COUNT = 1;
    private int AREAS_X = 400;
    private int AREAS_Y = 220;
    private int TIME_X = 53;
    private int TIME_Y = 220;
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
    private CheckBox projectLock;
    @FXML
    private Pane uploadPane;
    @FXML
    private JFXDatePicker date;

    @FXML
    public void upload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Log files", "*.*"));
        List<File> uploadedFiles = fileChooser.showOpenMultipleDialog(uploadButton.getScene().getWindow());
        Optional<List<Time>> times = gatherTimePoints();
        Optional<List<String>> fileNames = gatherFileNames();
        savePops();
        startCopying(uploadedFiles, fileNames, times);
    }

    @FXML
    void openBrowser(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URL("http://logs.tools.finanteq.com:5601/app/kibana#/management/kibana/index?_g=()").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void lock(ActionEvent actionEvent) {
        if (projectLock.isSelected()) {
            userName.setDisable(true);
            choiceBox.setDisable(true);
        } else {
            userName.setDisable(false);
            choiceBox.setDisable(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean value = Boolean.parseBoolean(loadProperties(PROJECT_PROP_LOCK));
        if (value && userName != null) {
            loadTextArea(userName, USER_PROP_NAME, USER_EMPTY);
            userName.setDisable(true);
            choiceBox.setDisable(true);
            projectLock.setSelected(true);
            choiceBox.setValue(loadProperties(PROJECT_PROP_NAME));
            String[] split = loadProperties(FILES_NAMES_PROP).substring(1).split(FLOOR);
            area0.setText(split[0]);
            for (int i = 0; i < split.length - 1; i++) {
                addTextFieldWithContent(split[i + 1]);
            }
        }
    }

    private void savePops() {
        saveProp(FILES_NAMES_PROP, fileNames);
        saveProp(USER_PROP_NAME, userName.getText().toLowerCase());
        saveProp(PROJECT_PROP_NAME, choiceBox.getValue());
        saveProp(PROJECT_PROP_LOCK, String.valueOf(projectLock.isSelected()));
        saveProp(FILES_NUMBER_PROP, String.valueOf(FILE_AREAS_COUNT - 1));
    }

    public void addTextField(ActionEvent actionEvent) {
        addTextFieldWithContent("");
    }

    public void subtractTextField(ActionEvent actionEvent) {
        ObservableList<Node> children = uploadPane.getChildren();
        if (FILE_AREAS_COUNT > 1) {
            FILE_AREAS_COUNT--;
            AREAS_Y -= 30;
        }
        String lastTextField = "area" + String.valueOf(FILE_AREAS_COUNT);
        children.removeIf(i -> lastTextField.equals(i.getId()));
    }

    private void addTextFieldWithContent(String content) {
        TextField textField = new TextField();
        textField.setText(content);
        textField.setId("area" + String.valueOf(FILE_AREAS_COUNT));
        textField.setLayoutX(AREAS_X);
        textField.setLayoutY(AREAS_Y);
        uploadPane.getChildren().add(textField);
        FILE_AREAS_COUNT++;
        AREAS_Y += 30;
    }

    public void addTime(ActionEvent actionEvent) {

        JFXTimePicker jfxTimePicker = new JFXTimePicker();
        jfxTimePicker.setDefaultColor(Color.valueOf("#ff5400"));
        jfxTimePicker.setPrefWidth(149.0);
        jfxTimePicker.setPromptText("Wybierz czas");
        jfxTimePicker.setId("time" + String.valueOf(TIME_COUNT));
        jfxTimePicker.setLayoutY(TIME_Y);
        jfxTimePicker.setLayoutX(TIME_X);
        uploadPane.getChildren().add(jfxTimePicker);
        TIME_COUNT++;
        TIME_Y += 30;
    }

    public void subtractTime(ActionEvent actionEvent) {
        ObservableList<Node> children = uploadPane.getChildren();
        if (TIME_COUNT > 1) {
            TIME_COUNT--;
            TIME_Y -= 30;
        }
        String lastTextField = "time" + String.valueOf(TIME_COUNT);
        children.removeIf(i -> lastTextField.equals(i.getId()));
    }

    private Optional<List<Time>> gatherTimePoints() {
        LocalDate datee = date.getValue();

        if (datee == null) return Optional.empty();

        List<LocalTime> localTimes = uploadPane.getChildren()
                .stream()
                .filter(i -> i.getId() != null && i.getId().contains("time"))
                .map(i -> ((JFXTimePicker) i).getValue())
                .collect(Collectors.toList());

        return Optional.of(prepareTimePoints(datee, localTimes, offset.getValue()));
    }

    private Optional<List<String>> gatherFileNames() {
        fileNames = "";
        List<String> files = new ArrayList<>();
        for (Node node : uploadPane.getChildren()) {
            if (node.getId() != null && node.getId().contains("area")) {
                String fileName = ((TextField) node).getText();
                fileNames += FLOOR + fileName;
                files.add(fileName);
            }
        }
        if(fileNames.isEmpty())
            return Optional.empty();
        else
            return Optional.of(files);
    }
}