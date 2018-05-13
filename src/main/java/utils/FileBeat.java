package utils;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import net.lingala.zip4j.core.ZipFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import static utils.FileUtils.clearDir;
import static utils.PropertiesUtils.loadProperties;

public final class FileBeat {
    private static final String USER_NAME_PROP = "user";
    private static final String PROJECT_NAME_PROP = "project";
    private static final String PROCESS_NAME = "filebeat";
    private static final String LOCATION = "location";
    private static String BEATS_DIR;
    private static String UNZIP_LOCATION;

    public static void startFilebeat() {
        try {
            UNZIP_LOCATION = loadProperties(LOCATION);
            BEATS_DIR = UNZIP_LOCATION + "FileBeat";
            if (checkIfProcessIsRunning(PROCESS_NAME)) {
                Runtime.getRuntime().exec("taskkill /F /IM filebeat.exe");
                Thread.sleep(200);
            }
            unZipFileBeat(FileBeat.class.getClass().getResource("/filebeat").getPath(), UNZIP_LOCATION);
            createFileBeatYml(BEATS_DIR);
            clearDir(BEATS_DIR + "\\tempFiles");
            clearDir(BEATS_DIR + "\\logz");
            ProcessBuilder cmd = new ProcessBuilder("cmd", "/c", BEATS_DIR + "\\filebeat.exe  -c " + BEATS_DIR + "\\filebeat.yml");
            cmd.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfProcessIsRunning(String processName) {
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

    private static void createFileBeatYml(String dir) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("filebeat.prospectors:" + "\n");
        builder.append("- type: log" + "\n");
        builder.append("  enabled: true" + "\n");
        builder.append("  paths:" + "\n");
        builder.append("    - " + dir + "\\logz\\*.log" + "\n");
        builder.append("  fields:" + "\n");
        builder.append("    user: " + loadProperties(PROJECT_NAME_PROP) + "_" + loadProperties(USER_NAME_PROP) + "\n");
        builder.append("  multiline.pattern: '\\[[0-9]{4}-[0-9]{2}-[0-9]{2} ([0-9]{2}:){2}[0-9]{2},[0-9]{3}\\+[0-9]{2}:[0-9]{2}\\]|\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}:\\d{3}'" + "\n");
        builder.append("  multiline.negate: true" + "\n");
        builder.append("  multiline.match: after" + "\n");
        builder.append("output.logstash:" + "\n");
        builder.append("  hosts: [\"logs.tools.finanteq.com:5044\"]" + "\n");

        Files.write(Paths.get(dir + "\\filebeat.yml"), builder.toString().getBytes());
    }

    public static void unZipFileBeat(String source, String destination) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(source);
            try {
                zipFile.extractAll(destination);
            } catch (net.lingala.zip4j.exception.ZipException e) {
                e.printStackTrace();
            }
        } catch (net.lingala.zip4j.exception.ZipException e) {
        }
    }

    public static Thread fileBeatNotifier(Label label) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (checkIfProcessIsRunning(PROCESS_NAME)) {
                        label.setTextFill(Color.GREEN);
                    } else {
                        label.setTextFill(Color.RED);
                    }
                }
            }
        });
    }
}
