package com.pandaism.sftaskflow.application.data.database;

import com.pandaism.sftaskflow.application.SFTaskFlowApplication;
import com.pandaism.sftaskflow.application.data.helper.WorkOrder;
import com.pandaism.sftaskflow.application.data.util.DatabaseConfig;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public abstract class Database {
    private final String type;
    private final DatabaseConfig databaseConfig;

    public Database(String type) {
        this.type = type;
        this.databaseConfig = SFTaskFlowApplication.databaseConfig;
    }

    protected Connection getConnection() {
        // Retrieve database connection properties from the ProductionScheduler class
        String username = this.databaseConfig.getUser(this.type);
        String password = this.databaseConfig.getPassword(this.type);
        String url = this.databaseConfig.getUrl(this.type);

        // Connect to the database using the provided connection properties
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public abstract Task<List<WorkOrder>> queryPastToAdvance30Days(HashSet<String> inProgressIDs);
    public abstract Task<HashSet<String>> queryInProgress();

    public String getType() {
        return type;
    }
}
