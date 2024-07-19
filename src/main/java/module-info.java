module com.pandaism.sftaskflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.swing;
    requires org.kordamp.ikonli.material2;
    requires java.desktop;
    requires java.sql;
    requires org.apache.poi.ooxml;

    opens com.pandaism.sftaskflow to javafx.fxml;
    exports com.pandaism.sftaskflow;
    opens com.pandaism.sftaskflow.application to javafx.fxml;
    exports com.pandaism.sftaskflow.application;
    opens com.pandaism.sftaskflow.application.data.helper to javafx.fxml;
    exports com.pandaism.sftaskflow.application.data.helper;
    opens com.pandaism.sftaskflow.application.data.util to javafx.fxml;
    exports com.pandaism.sftaskflow.application.data.util;
}