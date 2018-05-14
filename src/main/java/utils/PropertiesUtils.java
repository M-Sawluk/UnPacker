package utils;

import javafx.scene.control.TextInputControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertiesUtils {
    private static final String LOCATION = System.getProperty("java.io.tmpdir")+"\\logz.properties";

    public static void saveProp(String key, String value) {
        Path logPropsFilePath = Paths.get(LOCATION);
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

    public static String loadProperties(String propName) {
        Path logPropsFilePath = Paths.get(LOCATION);
        Properties properties = new Properties();
        FileInputStream resourceAsStream = null;
        try {
            if (Files.notExists(logPropsFilePath)) {
                createPropFile();
            }
            resourceAsStream = new FileInputStream(logPropsFilePath.toFile());
            properties.load(resourceAsStream);
            return properties.getProperty(propName);

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
        return "";
    }

    private static void createPropFile() throws IOException {
        Path logPropsFilePath = Paths.get(LOCATION);
        FileOutputStream fileOutputStream = new FileOutputStream(logPropsFilePath.toFile());
        Properties properties = new Properties();
        properties.setProperty("location", "");
        properties.setProperty("files", "");
        properties.setProperty("user", "");
        properties.setProperty("kibana_url","logs.tools.finanteq.com");
        properties.store(fileOutputStream, null);
    }

    public static void loadTextArea(TextInputControl control, String propName, String emptyText) {
        String prop = loadProperties(propName);
        if (prop.isEmpty()) {
            control.setText(emptyText);
        } else if (control != null){
            control.setText(prop);
        }
    }
}
