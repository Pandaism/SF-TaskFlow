package com.pandaism.sftaskflow.application.data.helper;

import java.util.List;

public class Help {
    public static double calculateTotalLabourHour(List<WorkOrder> workOrders) {
        double sum = 0;
        for(WorkOrder workOrder : workOrders) {
            sum += workOrder.getLabourHour().getValue();
        }

        return sum;
    }
}

