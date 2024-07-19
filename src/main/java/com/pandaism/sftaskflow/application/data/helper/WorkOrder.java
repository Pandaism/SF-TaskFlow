package com.pandaism.sftaskflow.application.data.helper;

import javafx.beans.property.*;

import java.text.DecimalFormat;

public class WorkOrder {
    private final String dueDate;
    private final boolean inProgress;
    private final String userField;
    private final String jobStartDate;
    private String type;
    private final String storeCode;
    private final String workOrderNumber;
    private final String partID;
    private final String description;
    private final int quantity;
    private final double labourHour;
    private final String salesOrderNumber;

    public WorkOrder(String dueDate, boolean inProgress, String userField, String jobStartDate, String type, String storeCode, String workOrderNumber, String partID, String description, int quantity, double labourHour, String salesOrderNumber) {
        this.dueDate = dueDate.substring(0, 10);
        this.inProgress = inProgress;
        this.userField = userField;
        this.jobStartDate = jobStartDate;
        this.type = type;
        this.storeCode = storeCode;
        this.workOrderNumber = workOrderNumber;
        this.partID = partID;
        this.description = description;
        this.quantity = quantity;
        DecimalFormat df = new DecimalFormat("#.####");
        this.labourHour = Double.parseDouble(df.format(labourHour));
        this.salesOrderNumber = salesOrderNumber;
    }

    public SimpleStringProperty getSalesOrderNumber() {
        return new SimpleStringProperty(this.salesOrderNumber);
    }
    public SimpleStringProperty getDueDate() {
        return new SimpleStringProperty(this.dueDate);
    }

    public SimpleBooleanProperty isInProgress() {
        return new SimpleBooleanProperty(this.inProgress);
    }

    public SimpleStringProperty getType() {
        return new SimpleStringProperty(this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public SimpleStringProperty getAssignedUser() {return new SimpleStringProperty(this.userField);}
    public SimpleStringProperty getJobStartDate () {return new SimpleStringProperty(this.jobStartDate);}

    public SimpleStringProperty getStoreCode() {
        return new SimpleStringProperty(this.storeCode);
    }

    public SimpleStringProperty getWorkOrderNumber() {
        return new SimpleStringProperty(this.workOrderNumber);
    }

    public SimpleStringProperty getPartID() {
        return new SimpleStringProperty(this.partID);
    }

    public SimpleStringProperty getDescription() {
        return new SimpleStringProperty(this.description);
    }

    public SimpleIntegerProperty getQuantity() {
        return new SimpleIntegerProperty(this.quantity);
    }

    public SimpleDoubleProperty getLabourHour() {
        return new SimpleDoubleProperty(this.labourHour);
    }

    public String display() {
        return this.getDueDate().getValue() + ", " + this.isInProgress().getValue() + ", " + this.getAssignedUser().getValue() + ", " + this.getType().getValue() + ", " + this.getSalesOrderNumber().getValue() + ", " + this.getStoreCode().getValue() + ", " + this.getWorkOrderNumber().getValue() + ", " + this.getPartID().getValue() + ", " + this.getDescription().getValue().replaceAll(",", ";") + ", " + this.getQuantity().getValue() + ", " + this.getLabourHour().getValue() + ", " + this.getJobStartDate().getValue();
    }
}
