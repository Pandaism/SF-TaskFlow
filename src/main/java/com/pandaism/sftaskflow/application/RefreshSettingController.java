package com.pandaism.sftaskflow.application;

import com.pandaism.sftaskflow.application.ui.popup.Messages;
import javafx.scene.control.ComboBox;

public class RefreshSettingController {
    public ComboBox<String> refreshTimeComboBox;

    public void initialize() {
        for(TimeSelection timeSelection : TimeSelection.values()) {
            this.refreshTimeComboBox.getItems().add(timeSelection.getDescription());
        }
    }

    public void onSave() {
        SFTaskFlowApplication.settings.saveRefreshTime(TimeSelection.getMinutes(this.refreshTimeComboBox.getValue()));

        Messages.displayInfoMessage("Refresh Timer Updated", "Refresh Timer has been updated to " + this.refreshTimeComboBox.getSelectionModel().getSelectedItem());
    }

    public enum TimeSelection {
        EVERY_1M("Every 1 minutes"), EVERY_2M("Every 2 minutes"), EVERY_5M("Every 5 minutes"), EVERY_10M("Every 10 minutes"), OFF("Off");

        private final String description;
        TimeSelection(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static int getMinutes(String description) {
            return switch (description) {
                case "Every 1 minutes" -> 1;
                case "Every 2 minutes" -> 2;
                case "Every 10 minutes" -> 10;
                case "Off" -> 1440;
                default -> 5;
            };
        }
    }
}
