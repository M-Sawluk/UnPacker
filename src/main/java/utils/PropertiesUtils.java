package utils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
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

    public static void saveProp(String key, String value) {
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

    public static String loadProperties(String propName) {
        Path logPropsFilePath = Paths.get("logz.properties");
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
        Path logPropsFilePath = Paths.get("logz.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(logPropsFilePath.toFile());
        Properties properties = new Properties();
        properties.setProperty("location", "");
        properties.setProperty("files", "");
        properties.setProperty("user", "");
        properties.store(fileOutputStream, null);
    }

    public static void loadCheckBox(CheckBox checkBox, String propName) {
        String prop = loadProperties(propName);
        boolean value = Boolean.parseBoolean(prop);
        checkBox.setSelected(value);
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
