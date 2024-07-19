package com.pandaism.sftaskflow.application.data.util;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class SettingsFile {
    private final File file = new File("./settings.properties");
    private final Properties properties;
    private final Map<String, String> defaultSettings = Map.of(
            "general.refreshTimer", "5"
    );
    public SettingsFile() {
        this.properties = new Properties();

        if(!this.file.exists()) {
            try {
                if(this.file.createNewFile()) {
                    for(String key : defaultSettings.keySet()) {
                        this.properties.setProperty(key, defaultSettings.get(key));
                    }

                    writeToProp();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (InputStream input = new BufferedInputStream(new FileInputStream(this.file))) {
            this.properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkSettings();

    }

    private void checkSettings() {
        // Check if user setting file has all the setting needed, if not add default values
        for(String settingHeader : this.defaultSettings.keySet()) {
            if(this.properties.getProperty(settingHeader) == null) {
                this.properties.setProperty(settingHeader, this.defaultSettings.get(settingHeader));
            }
        }

        writeToProp();
    }

    public void saveRefreshTime(int time) {
        this.properties.setProperty("general.refreshTimer", String.valueOf(time));
        writeToProp();
    }

    private void writeToProp() {
        try (FileOutputStream fos = new FileOutputStream(this.file)) {
            this.properties.store(fos, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRefreshTimer() {
        return Integer.parseInt(this.properties.getProperty("general.refreshTimer"));
    }
}
