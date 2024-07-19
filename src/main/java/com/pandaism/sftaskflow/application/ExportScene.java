package com.pandaism.sftaskflow.application;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ExportScene {
    public ExportScene(SFTaskFlowController taskFlowController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SFTaskFlowApplication.class.getResource("/com/pandaism/sftaskflow/application/export-view.fxml"));
        Parent root = fxmlLoader.load();
        ExportController controller = fxmlLoader.getController();
        controller.setTaskFlowController(taskFlowController);

        Scene scene = new Scene(root, 280, 250);
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        Stage stage = new Stage();
        stage.setTitle("Export");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
