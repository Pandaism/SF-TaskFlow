package com.pandaism.sftaskflow.application.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class TitledContainer extends BorderPane {
    public TitledContainer(Label title, Node content, double width, double height) {
        this.setPrefWidth(width);
        this.setPrefHeight(height);

        HBox titleTextContainer = new HBox(title);
        titleTextContainer.setStyle("-fx-background-color:#345f92");
        titleTextContainer.setAlignment(Pos.CENTER);
        titleTextContainer.setPrefHeight(height * .25);
        this.setTop(titleTextContainer);

        HBox contentContainer = new HBox(content);
        contentContainer.setStyle("-fx-background-color:#2a2d32");
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setPrefHeight(height * .75);
        this.setCenter(contentContainer);
    }
}
