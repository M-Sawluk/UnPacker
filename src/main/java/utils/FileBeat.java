package utils;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import log.AppLogger;
import net.lingala.zip4j.core.ZipFile;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static utils.FileUtils.clearDir;
import static utils.PropertiesUtils.loadProperties;

public final class FileBeat {
    private static final Logger LOGGER = AppLogger.getLogger();
    private static final String USER_NAME_PROP = "user";
    private static final String PROJECT_NAME_PROP = "project";
    private static final String PROCESS_NAME = "filebeat";
    private static final String LOCATION = "location";
    private static final String KIBANA_URL_PROP = "kibana_url";
    private static String BEATS_DIR;
    private static String UNZIP_LOCATION;

    public static void startFilebeat() {
        try {
            UNZIP_LOCATION = loadProperties(LOCATION) + "\\";
            BEATS_DIR = UNZIP_LOCATION + "FileBeat";
            if (checkIfProcessIsRunning(PROCESS_NAME)) {
                Runtime.getRuntime().exec("taskkill /F /IM filebeat.exe");
                Thread.sleep(200);
            }
            unzip(FileBeat.class.getResourceAsStream("/filebeat"), new File(UNZIP_LOCATION));
            createFileBeatYml(BEATS_DIR);

            ProcessBuilder cmd = new ProcessBuilder("cmd", "/c", BEATS_DIR + "\\filebeat.exe  -c " + BEATS_DIR + "\\filebeat.yml");
            cmd.start();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getStackTrace());
        }
    }

    private static boolean checkIfProcessIsRunning(String processName) {
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
            LOGGER.error(e.getStackTrace());
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
        builder.append("  hosts: [\"" + loadProperties(KIBANA_URL_PROP) + ":5044\"]" + "\n");
        LOGGER.info("FileBEat.yml: " + builder);
        Files.write(Paths.get(dir + "\\filebeat.yml"), builder.toString().getBytes());
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

    public static void unzip(InputStream source, File target) throws IOException {
        final ZipInputStream zipStream = new ZipInputStream(source);
        ZipEntry nextEntry;
        while ((nextEntry = zipStream.getNextEntry()) != null) {
            final String name = nextEntry.getName();
            // only extract files
            if (!name.endsWith("/")) {
                final File nextFile = new File(target, name);

                final File parent = nextFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                    LOGGER.info("Creating dir: " + parent);
                }

                try (OutputStream targetStream = new FileOutputStream(nextFile)) {
                    LOGGER.info("Copying: " + nextFile.getName());
                    copy(zipStream, targetStream);
                }
            }
        }
    }

    private static void copy(final InputStream source, final OutputStream target) throws IOException {
        final int bufferSize = 4 * 1024;
        final byte[] buffer = new byte[bufferSize];

        int nextCount;
        while ((nextCount = source.read(buffer)) >= 0) {
            target.write(buffer, 0, nextCount);
        }
    }
}
