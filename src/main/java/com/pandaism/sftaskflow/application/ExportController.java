package com.pandaism.sftaskflow.application;

import com.pandaism.sftaskflow.application.data.helper.WorkOrder;
import com.pandaism.sftaskflow.application.data.util.DataExportFile;
import com.pandaism.sftaskflow.application.ui.popup.Messages;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportController {
    public CheckBox excelCheckBox;
    public CheckBox csvCheckBox;
    private SFTaskFlowController taskFlowController;
    public void setTaskFlowController(SFTaskFlowController taskFlowController) {
        this.taskFlowController = taskFlowController;
    }

    public void onExport() {
        List<TreeItem<WorkOrder>> selectedItems = this.taskFlowController.getTable().getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty()) {
            Messages.displayInfoMessage("Export Notification", "The currently visible table will be exported as no item is selected.");
            selectedItems = this.taskFlowController.getTable().getRoot().getChildren();
        }

        if(this.excelCheckBox.isSelected()) {
            exportAsExcel(selectedItems);
        }

        if(this.csvCheckBox.isSelected()) {
            exportAsCSV(selectedItems);
        }
    }

    private void exportAsExcel(List<TreeItem<WorkOrder>> selectedItems) {
        DataExportFile tempExportFile = new DataExportFile(DataExportFile.FILETYPE.EXCEL);

        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOutputStream = new FileOutputStream(tempExportFile.getExportFile())) {
            Sheet sheet = workbook.createSheet("Data Export");
            String[] header = {"Due Date", "In Progress", "Assigned", "Type", "Sales Order", "Store Code", "Work Order ID", "Part ID", "Description", "Quantity", "Labour Hours", "Started"};
            Row headerRow = sheet.createRow(0);
            for(int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

            int rowNum = 1;
            for (TreeItem<WorkOrder> item : selectedItems) {
                WorkOrder workOrder = item.getValue();
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(workOrder.getDueDate().getValue());
                row.createCell(1).setCellValue(workOrder.isInProgress().getValue());
                row.createCell(2).setCellValue(workOrder.getAssignedUser().getValue());
                row.createCell(3).setCellValue(workOrder.getType().getValue());
                row.createCell(4).setCellValue(workOrder.getSalesOrderNumber().getValue());
                row.createCell(5).setCellValue(workOrder.getStoreCode().getValue());
                row.createCell(6).setCellValue(workOrder.getWorkOrderNumber().getValue());
                row.createCell(7).setCellValue(workOrder.getPartID().getValue());
                row.createCell(8).setCellValue(workOrder.getDescription().getValue());
                row.createCell(9).setCellValue(workOrder.getQuantity().getValue());
                row.createCell(10).setCellValue(workOrder.getLabourHour().getValue());
                row.createCell(11).setCellValue(workOrder.getJobStartDate().getValue());
            }

            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Messages.displayInfoMessage("Data Exported", "Data have been exported to " + tempExportFile.getExportFile().getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void exportAsCSV(List<TreeItem<WorkOrder>> selectedItems) {
        DataExportFile tempExportFile = new DataExportFile(DataExportFile.FILETYPE.CSV);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempExportFile.getExportFile()));
            writer.write("Due Date, In Progress, Assigned, Type, Sales Order, Store Code, Work Order ID, Part ID, Description, Quantity, Labour Hours, Started\n");

            for(TreeItem<WorkOrder> item : selectedItems) {
                WorkOrder workOrder = item.getValue();
                String output = workOrder.display();
                writer.write(output + "\n");
            }

            writer.close();

            Messages.displayInfoMessage("Data Exported", "Data have been exported to " + tempExportFile.getExportFile().getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
