package com.pandaism.sftaskflow.application;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RefreshSettingScene {
    public RefreshSettingScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFTaskFlowApplication.class.getResource("/com/pandaism/sftaskflow/application/refresh-setting-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 280, 250);
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        Stage stage = new Stage();
        stage.setTitle("Refresh Setting");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
