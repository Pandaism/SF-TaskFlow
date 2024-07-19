package com.pandaism.sftaskflow.application;

import atlantafx.base.theme.CupertinoDark;
import com.pandaism.sftaskflow.application.data.util.DatabaseConfig;
import com.pandaism.sftaskflow.application.data.util.ITProdFilterList;
import com.pandaism.sftaskflow.application.data.util.SettingsFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SFTaskFlowApplication extends Application {
    public static ScheduledExecutorService scheduler;
    public static ITProdFilterList itProdFilterList;
    public static SettingsFile settings;
    public static DatabaseConfig databaseConfig;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFTaskFlowApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        stage.setTitle("SF TaskFlow");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }

    public void launchApplication() {
        scheduler = Executors.newScheduledThreadPool(1);
        itProdFilterList = new ITProdFilterList();
        settings = new SettingsFile();
        databaseConfig = new DatabaseConfig();
        launch();
    }
}