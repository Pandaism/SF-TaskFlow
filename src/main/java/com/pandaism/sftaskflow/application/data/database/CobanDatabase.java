package com.pandaism.sftaskflow.application.data.database;

import com.pandaism.sftaskflow.application.data.helper.WorkOrder;
import javafx.concurrent.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CobanDatabase extends Database {
    public CobanDatabase() {
        super("LAW");
    }

    @Override
    public Task<List<WorkOrder>> queryPastToAdvance30Days(HashSet<String> inProgressIDs) {
        return new Task<>() {
            @Override
            protected List<WorkOrder> call() {
                Connection connection;
                List<WorkOrder> workOrders = new ArrayList<>();
                if((connection = getConnection()) != null) {
                    try {
                        Date advance = Date.valueOf(LocalDate.now().plusDays(30));

                        String query = "WITH Modifications AS (\n" +
                                "\tSELECT\n" +
                                "\t\tJOB_ID,\n" +
                                "\t\tMAX(DATE_CREATED) AS DateCreated\n" +
                                "\tFROM ICFIT\n" +
                                "\t\tGROUP BY JOB_ID)\n" +
                                "SELECT DISTINCT\n" +
                                "\tJCFJM.JOB_ID, \n" +
                                "\tJCFJM.STORES_CODE, \n" +
                                "\tJCFJM.PART_ID, \n" +
                                "\tJCFJM.JOB_SCH_COMPL,\n" +
                                "\tJCFJM.REV_ORDER_QTY,\n" +
                                "\tICFPM.STD_1_HRS,\n" +
                                "\tJCFJM.JOB_DESC, \n" +
                                "\tJCFJM.JCFJM_USER_1, \n" +
                                "\tLM.DateCreated, \n" +
                                "\tJCFJM.SO_ID \n" +
                                "FROM COBANDB.dbo.JCFJM\n" +
                                "INNER JOIN\n" +
                                "\tICFPM ON JCFJM.PART_ID = ICFPM.PART_ID\n" +
                                "LEFT OUTER JOIN\n" +
                                "\tModifications LM ON JCFJM.JOB_ID = LM.JOB_ID\n" +
                                "WHERE\n" +
                                "\t(JCFJM.JOB_SCH_COMPL < ?) \n" +
                                "\tAND JCFJM.JOB_STATUS = 'O'\n" +
                                "\tAND JCFJM.STORES_CODE IN ('MS')\n" +
                                "ORDER BY JCFJM.JOB_SCH_COMPL;";

                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.setDate(1, advance);

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String jobID = rs.getString(1);
                                String storeCode = rs.getString(2);
                                String partID = rs.getString(3);
                                String dueDate = rs.getString(4);
                                int qty = rs.getInt(5);
                                double totalLaborHour = rs.getDouble(6) * qty;
                                String jobDescription = rs.getString(7);
                                String userField = rs.getString(8);
                                String dateCreated = rs.getString(9);
                                String saleOrderNumber = rs.getString(10);

                                WorkOrder workOrder;
                                if(inProgressIDs.contains(jobID)) {
                                    workOrder = new WorkOrder(dueDate, true, userField, dateCreated, getType(), storeCode, jobID, partID, jobDescription, qty, totalLaborHour, saleOrderNumber);
                                } else {
                                    workOrder = new WorkOrder(dueDate, false, userField, dateCreated, getType(), storeCode, jobID, partID, jobDescription, qty, totalLaborHour, saleOrderNumber);
                                }
                                workOrders.add(workOrder);
                            }
                        }
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return workOrders;
            }
        };
    }
    @Override
    public Task<HashSet<String>> queryInProgress() {
        return new Task<>() {
            @Override
            protected HashSet<String> call() {
                Connection connection;
                HashSet<String> jobIds = new HashSet<>();
                if((connection = getConnection()) != null) {
                    try {
                        String query = "SELECT \n" +
                                "\tJCFJM.STORES_CODE,\n" +
                                "\tJCFKL.JOB_ID, \n" +
                                "\tJCFKL.COMPONENT_ID,\n" +
                                "\tJCFKL.ISSUED_QTY\n" +
                                "FROM \n" +
                                "\tCOBANDB.dbo.JCFKL\n" +
                                "INNER JOIN\n" +
                                "\tJCFJM ON JCFKL.JOB_ID = JCFJM.JOB_ID\n" +
                                "WHERE\n" +
                                "\t(JCFKL.ISSUED_QTY != 0) AND (JCFJM.JOB_STATUS = 'O') AND (JCFJM.STORES_CODE IN ('MS'));";

                        PreparedStatement ps = connection.prepareStatement(query);

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String jobID = rs.getString(2);
                                jobIds.add(jobID);
                            }
                        }
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Connection failed");
                }
                return jobIds;
            }
        };
    }

}
