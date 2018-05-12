package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeWithFiles {
    private final Time time;
    private final Map<String, Boolean> fileUploadedStatusMap;

    public TimeWithFiles(Time time, List<String> files) {
        this.time = time;
        fileUploadedStatusMap = new HashMap<>();
        for (String file : files) {
            fileUploadedStatusMap.put(file, Boolean.FALSE);
        }
    }

    public void setUploaded(String fileName) {
        fileUploadedStatusMap
                .entrySet()
                .stream()
                .filter(i-> fileName != null && fileName.contains(i.getKey()))
                .forEach(i -> fileUploadedStatusMap.replace(i.getKey(), Boolean.TRUE));
    }


    public boolean isFileUploaded(String fileName) {
        return fileUploadedStatusMap
        .entrySet()
        .stream()
        .filter(i-> fileName != null && fileName.contains(i.getKey()))
        .map(Map.Entry::getValue)
        .findAny()
        .orElse(Boolean.TRUE);
    }

    public Time getTime() { return time; }
}
