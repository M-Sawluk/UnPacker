package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TimeWithFilesTest {

    @Test
    public void setUploaded() {
        TimeWithFiles timeWithFiles = new TimeWithFiles(null, Arrays.asList("webservices", "communication"));

        timeWithFiles.setUploaded("communication13413431413");
        boolean fileUploaded = timeWithFiles.isFileUploaded("communicationadasdsasdas");

        Assertions.assertTrue(fileUploaded);

    }

}