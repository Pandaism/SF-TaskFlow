package com.pandaism.sftaskflow.application.data.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataExportFile {
    private final File exportFile;

    public DataExportFile(FILETYPE filetype) {
        LocalDateTime now = LocalDateTime.now();
        this.exportFile = new File("./" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "." + filetype.getExtension());
    }

    public File getExportFile() {
        return exportFile;
    }

    public enum FILETYPE {
        EXCEL("xlsx"), CSV("csv");

        private final String extension;
        FILETYPE(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }
}
