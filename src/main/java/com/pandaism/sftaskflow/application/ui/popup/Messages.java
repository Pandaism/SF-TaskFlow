package com.pandaism.sftaskflow.application.ui.popup;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

public class Messages {

    public static void displayErrorMessage(String header, String message) {
        displayMessage(false, Alert.AlertType.ERROR, "Error Message", header, message);
    }

    public static void displayInfoMessage(String header, String message) {
        displayMessage(false, Alert.AlertType.INFORMATION, "Information Message", header, message);
    }

    public static void displayLongInfoMessage(String header, String message) {
        displayMessage(true, Alert.AlertType.INFORMATION, "Information Message", header, message);
    }

    private static void displayMessage(boolean mlong, Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        TextArea textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        if(mlong) {
            alert.getDialogPane().setExpandableContent(textArea);
        } else {
            alert.getDialogPane().setContentText(message);
        }
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        alert.setResizable(true);

        alert.show();
    }
}
