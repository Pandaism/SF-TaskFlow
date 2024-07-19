package com.pandaism.sftaskflow.application.data.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private final Properties properties;

    public DatabaseConfig() {
        this.properties = new Properties();
        load();
    }

    public String getUrl(String database) {
        return this.properties.getProperty(database.toLowerCase() + ".url");
    }

    public String getUser(String database) {
        return this.properties.getProperty(database.toLowerCase() + ".user");
    }

    public String getPassword(String database) {
        return this.properties.getProperty(database.toLowerCase() + ".password");
    }

    private void load() {
        try (InputStream input = new BufferedInputStream(new FileInputStream("X:\\Users\\MikeN\\Programming\\secrets\\database.properties"))) {
            this.properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
