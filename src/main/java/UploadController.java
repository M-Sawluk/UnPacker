import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class UploadController implements Initializable {
    private String TEMP_DIR;
    private String LOGS_DIR;
    private String[] filesToUpload;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField beatLocation;
    @FXML
    private TextArea area;
    @FXML
    private TextField userName;

    @FXML
    public void upload(ActionEvent event) throws IOException, InterruptedException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Log files", "*.*"));
        List<File> uploadedFiles = fileChooser.showOpenMultipleDialog(uploadButton.getScene().getWindow());
        String files = area.getText();
        saveProp("files", files);
        saveProp("user", userName.getText().toLowerCase());
        startFileBeat();
        filesToUpload = area.getText().split("\n");
        if (uploadedFiles != null && uploadedFiles.size() > 0) {
            for (File file : uploadedFiles) {
                unzipUploadedFiles(file);
            }
        }
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

    private void unzipUploadedFiles(File file) throws IOException {
        if (file.getName().endsWith(".zip")) {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (!zipEntry.isDirectory()) {
                    String entryName = zipEntry.getName();
                    int i = entryName.lastIndexOf(".");
                    File tempFile = stream2file(zipFile.getInputStream(zipEntry), entryName.substring(0, i), entryName.substring(i));
                    if (entryName.endsWith(".zip")) {
                        unzipUploadedFiles(tempFile);
                    } else {
                        if (isFileToUpload(tempFile.getName()) && !tempFile.getName().endsWith(".sha256sum")) {
                            if (tempFile.getName().contains(".log")) {
                                Files.copy(tempFile.toPath(), Paths.get(LOGS_DIR, "\\" + tempFile.getName()),
                                        StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                Files.copy(tempFile.toPath(), Paths.get(LOGS_DIR + "\\" + tempFile.getName() + ".log"),
                                        StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }
            zipFile.close();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                unzipUploadedFiles(file1);
            }
        } else if (isFileToUpload(file.getName())) {
            if(!file.getName().endsWith(".log")) {
                Files.move(file.toPath(), Paths.get(LOGS_DIR + "\\" + file.getName()+".log"), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(file.toPath(), Paths.get(LOGS_DIR + "\\" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

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
            TEMP_DIR = fileBeatPath + "\\tempFiles";
            LOGS_DIR = fileBeatPath + "\\logz";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
    }

    private File stream2file(InputStream in, String prefix, String suffix) throws IOException {
        final File tempFile = File.createTempFile(prefix, suffix, new File(TEMP_DIR));
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
            in.close();
            out.close();
        }
        return tempFile;
    }

    private boolean isFileToUpload(String fileName) {
        for (String s : filesToUpload) {
            if (fileName.contains(s)) return true;
        }
        return false;
    }

    private void loadProperties() {
        Path logPropsFilePath = Paths.get("logz.properties");
        Properties properties = new Properties();
        FileInputStream resourceAsStream = null;
        try {
            if (Files.notExists(logPropsFilePath)) {
                createPropFile();
            }
            resourceAsStream = new FileInputStream(logPropsFilePath.toFile());
            properties.load(resourceAsStream);
            String location = properties.getProperty("location");
            String files = properties.getProperty("files");
            String user = properties.getProperty("user");
            if (!location.isEmpty()) {
                beatLocation.setText(location);
                TEMP_DIR = location + "\\tempFiles";
                LOGS_DIR = location + "\\logz";
            } else {
                beatLocation.setText("Specify Beats Location");
            }
            if (!files.isEmpty()) {
                area.setText(files);
            } else {
                area.setText("Provide files names \neach in new line");
            }
            if (!user.isEmpty()) {
                userName.setText(user);
            } else {
                userName.setText("Enter name");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != resourceAsStream) {
                try {
                    resourceAsStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveProp(String key, String value) {
        Path logPropsFilePath = Paths.get("logz.properties");
        Properties properties = new Properties();
        FileInputStream resourceAsStream = null;
        try {
            if (Files.exists(logPropsFilePath)) {
                resourceAsStream = new FileInputStream(logPropsFilePath.toFile());
                properties.load(resourceAsStream);
            }
            File file = logPropsFilePath.toFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            properties.setProperty(key, value);
            properties.store(fileOutputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != resourceAsStream) {
                try {
                    resourceAsStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearDir(String path) {
        try {
            if (Files.notExists(Paths.get(path))) {
                new File(path).mkdirs();
            } else {
                FileUtils.deleteDirectory(new File(path));
                new File(path).mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPropFile() throws IOException {
        Path logPropsFilePath = Paths.get("logz.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(logPropsFilePath.toFile());
        Properties properties = new Properties();
        properties.setProperty("location", "");
        properties.setProperty("files", "");
        properties.setProperty("user", "");
        properties.store(fileOutputStream, null);
    }

    private void startFileBeat() throws IOException, InterruptedException {
        String uploadDir = beatLocation.getText();
        createFileBeatYml(uploadDir);
        if (checkIfProcessIsRunning("filebeat")) {
            Runtime.getRuntime().exec("taskkill /F /IM filebeat.exe");
            Thread.sleep(200);
        }
        clearDir(TEMP_DIR);
        clearDir(LOGS_DIR);
        ProcessBuilder cmd = new ProcessBuilder("cmd", "/c", uploadDir + "\\filebeat.exe  -c " + uploadDir + "\\filebeat.yml");
        cmd.start();
    }

    private boolean checkIfProcessIsRunning(String processName) {
        StringBuilder pidInfo = new StringBuilder();
        String line = "";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pidInfo.toString().contains(processName);
    }

    private void createFileBeatYml(String dir) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("filebeat.prospectors:" + "\n");
        builder.append("- type: log" + "\n");
        builder.append("  enabled: true" + "\n");
        builder.append("  paths:" + "\n");
        builder.append("    - " + dir + "\\logz\\*.log" + "\n");
        builder.append("  fields:" + "\n");
        builder.append("    user: " + userName.getText().toLowerCase() + "\n");
        builder.append("  multiline.pattern: '\\[[0-9]{4}-[0-9]{2}-[0-9]{2} ([0-9]{2}:){2}[0-9]{2},[0-9]{3}\\+[0-9]{2}:[0-9]{2}\\]|\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}:\\d{3}'" + "\n");
        builder.append("  multiline.negate: true" + "\n");
        builder.append("  multiline.match: after" + "\n");
        builder.append("output.logstash:" + "\n");
        builder.append("  hosts: [\"logs.tools.finanteq.com:5044\"]" + "\n");

        Files.write(Paths.get(dir + "\\filebeat.yml"), builder.toString().getBytes());
    }
}
