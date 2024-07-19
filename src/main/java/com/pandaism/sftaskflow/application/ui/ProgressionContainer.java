package com.pandaism.sftaskflow.application.ui;

import atlantafx.base.controls.RingProgressIndicator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProgressionContainer extends VBox {
    public ProgressionContainer(String title, double progress) {
        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(new Label(title));

        RingProgressIndicator progressIndicator = new RingProgressIndicator(progress);
        this.getChildren().add(progressIndicator);
    }
}
