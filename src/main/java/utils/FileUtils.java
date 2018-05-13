package utils;

import model.Time;
import model.TimeWithFiles;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static utils.DateUtils.getDate;
import static utils.PropertiesUtils.loadProperties;
import static utils.TimeUtils.prepareTimesWithFiles;

@SuppressWarnings("ALL")
public final class FileUtils {
    private static final String LOGS_LOCATION = "logs_location";
    private static final String DATE_REGEX = "\\[\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}\\,\\d{3}\\+\\d{2}:\\d{2}\\].* |" +
            "\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2}\\,\\d{3}.*";
    private static String LOGS_DIR = loadProperties(LOGS_LOCATION) + "\\logz";
    private static String TEMP_DIR = loadProperties(LOGS_LOCATION) + "\\temps";
    private static List<String> FILES_TO_UPLOAD;
    private static List<TimeWithFiles> TIME_WITH_FILES;

    static void clearDir(String path) {
        try {
            if (Files.notExists(Paths.get(path))) {
                new File(path).mkdirs();
            } else {
                org.apache.commons.io.FileUtils.deleteDirectory(new File(path));
                new File(path).mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startCopying(List<File> files, Optional<List<String>> fileNames, Optional<List<Time>> times) {
        if (files != null && files.size() > 0 && fileNames.isPresent()) {
            String logsDir = loadProperties(LOGS_LOCATION);
            FILES_TO_UPLOAD = fileNames.get();
            if (times.isPresent()) TIME_WITH_FILES = prepareTimesWithFiles(FILES_TO_UPLOAD, times.get());
            for (File file : files) {
                try {
                    copyUploadedFiles(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyUploadedFiles(File file) throws IOException {
        String name = file.getName();
        if (name.endsWith(".zip")) {
            copyFilesToDestFolderFromZip(file);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileFromDirectory : files) {
                copyUploadedFiles(fileFromDirectory);
            }
        } else if (isFileToUpload(name)) {
            if (name.endsWith(".log")) {
                copyFilesToDestinationFolder(file, "");
            } else {
                copyFilesToDestinationFolder(file, ".log");
            }
        }
    }

    private static void copyFilesToDestFolderFromZip(File zipedFile) throws IOException {
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipedFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (!zipEntry.isDirectory()) {
                String entryName = zipEntry.getName();
                int i = entryName.lastIndexOf(".");
                File tempFile = stream2file(zipFile.getInputStream(zipEntry), entryName.substring(0, i), entryName.substring(i));
                if (entryName.endsWith(".zip")) {
                    copyFilesToDestFolderFromZip(tempFile);
                } else {
                    String name = tempFile.getName();
                    if (isFileToUpload(name) && !name.endsWith(".sha256sum")) {
                        if (name.endsWith(".log")) {
                            copyFilesToDestinationFolder(tempFile, "");
                        } else {
                            copyFilesToDestinationFolder(tempFile, ".log");
                        }
                    }
                }
            }
        }
        zipFile.close();
    }

    private static void copyFilesToDestinationFolder(File file, String suffix) throws IOException {
        if (TIME_WITH_FILES == null || TIME_WITH_FILES.isEmpty()) {
            Files.copy(file.toPath(), Paths.get(LOGS_DIR + "\\" + file.getName() + suffix),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            List<TimeWithFiles> times = getTimes(file);
            if (!times.isEmpty()) {
                copyLinesByTimeOffest(file, times);
            }
        }
    }

    private static File stream2file(InputStream in, String prefix, String suffix) throws IOException {
        final File tempFile = File.createTempFile(prefix, suffix, new File(TEMP_DIR));
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
            in.close();
            out.close();
        }
        return tempFile;
    }

    private static boolean isFileToUpload(String fileName) {
        for (String s : FILES_TO_UPLOAD) {
            if (fileName.contains(s)) return true;
        }
        return false;
    }

    private static List<TimeWithFiles> getTimes(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        String firstLine = raf.readLine();
        String name = file.getName();
        if (firstLine.matches(DATE_REGEX)) {
            LocalDateTime dateTime = getDate(firstLine);
            return TIME_WITH_FILES
                    .stream()
                    .filter(i -> dateTime.isEqual(i.getTime().getSelectedTime().toLocalDate().atStartOfDay()) ||
                            dateTime.isAfter(i.getTime().getSelectedTime().toLocalDate().atStartOfDay()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static void copyLinesByTimeOffest(File file, List<TimeWithFiles> times) throws IOException {
        String name = file.getName();
        LocalDateTime logDateTime = null;
        LocalDateTime from = null;
        LocalDateTime to = null;

        List<String> lines = Files.readAllLines(file.toPath());
        List<String> validLines = new LinkedList<>();
        boolean isDateOfLinesValid = false;
        for (TimeWithFiles time : times) {
            if(time.isFileUploaded(name)) continue;
            for (String string : lines) {
                if (string.matches(DATE_REGEX)) {
                    logDateTime = getDate(string);
                    if (logDateTime.isAfter(time.getTime().getFrom()) && logDateTime.isBefore(time.getTime().getTo())) {
                        if (from == null) from = logDateTime;
                        to = logDateTime;
                        isDateOfLinesValid = true;
                        validLines.add(string);
                        time.setUploaded(name);
                    } else if (logDateTime.isAfter(time.getTime().getTo())) {
                        break;
                    }
                } else if (isDateOfLinesValid) {
                    validLines.add(string);
                }
            }
            isDateOfLinesValid = false;
        }
        if(!validLines.isEmpty())
        saveLines(file, from, to, validLines, name);
    }

    private static void saveLines(File file, LocalDateTime from, LocalDateTime to, List<String> validLines, String name) {
        File file_temp = new File(LOGS_DIR, "\\" + createPrettyFileName(file) + "_from_" + escapeColons(from) + "_to_" + escapeColons(to) + ".log");
        try {
            FileWriter writer = new FileWriter(file_temp);
            for (String str : validLines) {
                writer.write(str + System.getProperty("line.separator"));
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing the file : ");
            e.printStackTrace();
        }
    }

    private static String createPrettyFileName(File file) {
        String name = file.getName();
        return FILES_TO_UPLOAD
                .stream()
                .filter(i -> name.contains(i))
                .findFirst()
                .orElse("NAME NOT FOUND");
    }

    private static String escapeColons(LocalDateTime time) {
        if (time == null) return "TIME_NOT_FOUND";
        return time.toString().replace(':', '.');
    }

}
