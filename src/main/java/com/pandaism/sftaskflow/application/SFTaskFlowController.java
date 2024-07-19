package com.pandaism.sftaskflow.application;

import com.pandaism.sftaskflow.application.data.database.CobanDatabase;
import com.pandaism.sftaskflow.application.data.database.FMDatabase;
import com.pandaism.sftaskflow.application.data.database.SeonDatabase;
import com.pandaism.sftaskflow.application.data.helper.Help;
import com.pandaism.sftaskflow.application.data.helper.WorkOrder;
import com.pandaism.sftaskflow.application.ui.ProgressionContainer;
import com.pandaism.sftaskflow.application.ui.TitledContainer;
import com.pandaism.sftaskflow.application.ui.popup.Messages;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SFTaskFlowController {
    // Top
    public GridPane labourHourContainer;
    public Pane aheadOfScheduleContainer;
    // Center
    public Pane tableContainer;
    // Bottom
    public Label lastRefreshedLabel;
    public HBox filterContainer;

    private TreeTableView<WorkOrder> table;
    private List<WorkOrder> allWorkOrders;
    private boolean made = false;
    private CheckBox seonCheckbox;
    private CheckBox itCheckbox;
    private CheckBox cobanCheckbox;
    private CheckBox fleetmindCheckbox;
    private CountDownLatch countDownLatch;
    private final AtomicReference<Double> totalLabourHoursSeon = new AtomicReference<>(0D);
    private final AtomicReference<List<WorkOrder>> seonWorkOrders = new AtomicReference<>();
    private final AtomicReference<Double> totalLabourHoursCoban = new AtomicReference<>(0D);
    private final AtomicReference<List<WorkOrder>> cobanWorkOrders = new AtomicReference<>();
    private final AtomicReference<Double> totalLabourHoursFM = new AtomicReference<>(0D);
    private final AtomicReference<List<WorkOrder>> fmWorkOrders = new AtomicReference<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(7);
    private int initialRefreshTime = 0;

    public void initialize() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            int newRefreshTime = SFTaskFlowApplication.settings.getRefreshTimer();
            if(newRefreshTime != this.initialRefreshTime) {
                SFTaskFlowApplication.scheduler.scheduleWithFixedDelay(this::processTasks, 0, newRefreshTime, TimeUnit.MINUTES);
                this.initialRefreshTime = newRefreshTime;
            }
        }, 0, 1, TimeUnit.MINUTES);

    }

    private void processTasks() {
        this.countDownLatch = new CountDownLatch(7);

        Task<Void> refreshITProdFilterList = SFTaskFlowApplication.itProdFilterList.refresh();
        refreshITProdFilterList.setOnSucceeded(event -> {
            countDownLatch.countDown();

            SeonDatabase seonDatabase = new SeonDatabase();
            Task<HashSet<String>> seonInProgressQuery = seonDatabase.queryInProgress();
            seonInProgressQuery.setOnSucceeded(event1 -> {
                HashSet<String> inProgressJobIDs = seonInProgressQuery.getValue();
                Task<List<WorkOrder>> queried30AdvanceTask = seonDatabase.queryPastToAdvance30Days(inProgressJobIDs);
                queried30AdvanceTask.setOnSucceeded(event2 -> {
                    seonWorkOrders.set(queried30AdvanceTask.getValue());
                    countDownLatch.countDown();
                    Task<Double> calculateLabourHourTask = createCalculateLabourHourTask(seonWorkOrders, totalLabourHoursSeon);
                    executorService.submit(calculateLabourHourTask);
                });
                executorService.submit(queried30AdvanceTask);
            });
            this.executorService.submit(seonInProgressQuery);
        });
        this.executorService.submit(refreshITProdFilterList);

        CobanDatabase cobanDatabase = new CobanDatabase();
        Task<HashSet<String>> cobanInProgressQuery = cobanDatabase.queryInProgress();
        cobanInProgressQuery.setOnSucceeded(event -> {
            HashSet<String> inProgressJobIDs = cobanInProgressQuery.getValue();
            Task<List<WorkOrder>> queried30AdvanceTask = cobanDatabase.queryPastToAdvance30Days(inProgressJobIDs);
            queried30AdvanceTask.setOnSucceeded(event1 -> {
                cobanWorkOrders.set(queried30AdvanceTask.getValue());
                countDownLatch.countDown();
                Task<Double> calculateLabourHourTask = createCalculateLabourHourTask(cobanWorkOrders, totalLabourHoursCoban);
                executorService.submit(calculateLabourHourTask);
            });
            executorService.submit(queried30AdvanceTask);
        });
        executorService.submit(cobanInProgressQuery);

        FMDatabase fmDatabase = new FMDatabase();
        Task<HashSet<String>> fmInProgressQuery = fmDatabase.queryInProgress();
        fmInProgressQuery.setOnSucceeded(event -> {
            HashSet<String> inProgressJobIDs = fmInProgressQuery.getValue();
            Task<List<WorkOrder>> queried30AdvanceTask = fmDatabase.queryPastToAdvance30Days(inProgressJobIDs);
            queried30AdvanceTask.setOnSucceeded(event1 -> {
                fmWorkOrders.set(queried30AdvanceTask.getValue());
                countDownLatch.countDown();
                Task<Double> calculateLabourHourTask = createCalculateLabourHourTask(fmWorkOrders, totalLabourHoursFM);
                executorService.submit(calculateLabourHourTask);
            });
            executorService.submit(queried30AdvanceTask);
        });
        executorService.submit(fmInProgressQuery);

        initializeBottomContainer();
    }

    private Task<Double> createCalculateLabourHourTask(AtomicReference<List<WorkOrder>> workOrders, AtomicReference<Double> totalLabourHours) {
        return new Task<>() {
            @Override
            protected Double call() {
                return Help.calculateTotalLabourHour(workOrders.get());
            }

            @Override
            protected void succeeded() {
                totalLabourHours.set(getValue());
                countDownLatch.countDown();
                checkAndInitializeTopContainer(countDownLatch,
                        totalLabourHoursSeon.get(), seonWorkOrders.get(),
                        totalLabourHoursCoban.get(), cobanWorkOrders.get(),
                        totalLabourHoursFM.get(), fmWorkOrders.get());
            }
        };
    }

    private void checkAndInitializeTopContainer(CountDownLatch latch, double seonTotalLabourHours, List<WorkOrder> seonWorkOrders, double cobanTotalLabourHours, List<WorkOrder> cobanWorkOrders, double fmTotalLabourHours, List<WorkOrder> fmWorkOrders) {
        if (latch.getCount() == 0) {
            Platform.runLater(() -> {
                initializeTopContainer(seonTotalLabourHours, cobanTotalLabourHours, fmTotalLabourHours);

                this.tableContainer.getChildren().clear();
                initializeMiddleContainer(seonWorkOrders, cobanWorkOrders, fmWorkOrders);
            });
        }
    }

    private void initializeTopContainer(double seonLabourHours, double cobanLabourHours, double fmLabourHours) {
        TitledContainer weeklyLabourHourContainer = new TitledContainer(new Label("Total 30 days Labour Hours"), new Label(String.valueOf(new DecimalFormat("#.##").format(seonLabourHours + cobanLabourHours + fmLabourHours))), 150, 120);
        TitledContainer seonLHContainer = new TitledContainer(new Label("Seon LH"), new Label(String.valueOf(new DecimalFormat("#.##").format(seonLabourHours))), 100, 60);
        TitledContainer cobanLHContainer = new TitledContainer(new Label("Coban LH"), new Label(String.valueOf(new DecimalFormat("#.##").format(cobanLabourHours))), 100, 60);
        TitledContainer fleetMindLHContainer = new TitledContainer(new Label("FleetMind LH"), new Label(String.valueOf(new DecimalFormat("#.##").format(fmLabourHours))), 100, 60);
        labourHourContainer.add(weeklyLabourHourContainer, 0, 0, 1, 2);
        labourHourContainer.add(seonLHContainer, 1, 0, 1, 1);
        labourHourContainer.add(cobanLHContainer, 2, 0, 1, 1);
        labourHourContainer.add(fleetMindLHContainer, 3, 0, 1, 1);

        if(!made) {
            HBox checkboxContainer = new HBox();
            checkboxContainer.setAlignment(Pos.CENTER);
            checkboxContainer.setSpacing(20);

            this.seonCheckbox = new CheckBox("PT");
            this.itCheckbox = new CheckBox("IT PROD");
            this.cobanCheckbox = new CheckBox("LAW");
            this.fleetmindCheckbox = new CheckBox("WASTE");
            this.seonCheckbox.setSelected(true);
            this.itCheckbox.setSelected(true);
            this.cobanCheckbox.setSelected(true);
            this.fleetmindCheckbox.setSelected(true);

            this.seonCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> tableFiltering());
            this.itCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> tableFiltering());
            this.cobanCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> tableFiltering());
            this.fleetmindCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> tableFiltering());

            checkboxContainer.getChildren().addAll(this.seonCheckbox, itCheckbox, this.cobanCheckbox, this.fleetmindCheckbox);
            TitledContainer filterContainer = new TitledContainer(new Label("Filters"), checkboxContainer, 450, 60);
            this.filterContainer.setAlignment(Pos.CENTER);
            this.filterContainer.getChildren().add(filterContainer);
            made = true;
        }

        HBox progressionContainer = new HBox();
        progressionContainer.setSpacing(20);
        progressionContainer.getChildren().add(new ProgressionContainer("Seon", 1));
        progressionContainer.getChildren().add(new ProgressionContainer("Coban", 1));
        progressionContainer.getChildren().add(new ProgressionContainer("FleetMind", 1));

        // TODO Get Ahead of Schedule calculation to work
         TitledContainer aheadOfScheduleContainer = new TitledContainer(new Label("Non-functional Ahead of Schedule"), progressionContainer, 250, 120);
         this.aheadOfScheduleContainer.getChildren().add(aheadOfScheduleContainer);
    }

    private void initializeMiddleContainer(List<WorkOrder> seonWorkOrders, List<WorkOrder> cobanWorkOrders, List<WorkOrder> fmWorkOrders) {
        this.allWorkOrders = new ArrayList<>(Stream.of(seonWorkOrders, cobanWorkOrders, fmWorkOrders).flatMap(Collection::stream).toList());
        this.allWorkOrders.sort((o1, o2) -> {
            if (o1.getDueDate() == null || o2.getDueDate() == null) return 0;

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return format.parse(o1.getDueDate().getValue()).compareTo(format.parse(o2.getDueDate().getValue()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        TreeTableColumn<WorkOrder, String> dueDateCol = new TreeTableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(param -> param.getValue().getValue().getDueDate());

        TreeTableColumn<WorkOrder, String> inProgressCol = new TreeTableColumn<>("In Progress");
        inProgressCol.setCellValueFactory(param -> {
            if (param.getValue().getValue().isInProgress().getValue()) {
                return new SimpleStringProperty("✓");
            }
            return null;
        });

        TreeTableColumn<WorkOrder, String> assignedUserFieldCol = new TreeTableColumn<>("Assigned");
        assignedUserFieldCol.setCellValueFactory(param -> param.getValue().getValue().getAssignedUser());

        TreeTableColumn<WorkOrder, String> jobStartDate = new TreeTableColumn<>("Started");
        jobStartDate.setCellValueFactory(param -> param.getValue().getValue().getJobStartDate());

        TreeTableColumn<WorkOrder, String> typeCol = new TreeTableColumn<>("Type");
        typeCol.setCellValueFactory(param -> param.getValue().getValue().getType());

        TreeTableColumn<WorkOrder, String> salesOrderCol = new TreeTableColumn<>("Sales Order");
        salesOrderCol.setCellValueFactory(param -> param.getValue().getValue().getSalesOrderNumber());

        TreeTableColumn<WorkOrder, String> storeCodeCol = new TreeTableColumn<>("Store Code");
        storeCodeCol.setCellValueFactory(param -> param.getValue().getValue().getStoreCode());

        TreeTableColumn<WorkOrder, String> workOrderCol = new TreeTableColumn<>("Work Order ID");
        workOrderCol.setCellValueFactory(param -> param.getValue().getValue().getWorkOrderNumber());

        TreeTableColumn<WorkOrder, String> partIDCol = new TreeTableColumn<>("Part ID");
        partIDCol.setCellValueFactory(param -> param.getValue().getValue().getPartID());

        TreeTableColumn<WorkOrder, String> descriptionCol = new TreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().getDescription());

        TreeTableColumn<WorkOrder, String> quantityCol = new TreeTableColumn<>("Quantity");
        quantityCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue().getQuantity().getValue())));

        TreeTableColumn<WorkOrder, String> labourHourCol = new TreeTableColumn<>("Labour Hours");
        labourHourCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue().getLabourHour().getValue())));

        this.table = new TreeTableView<>(new TreeItem<>());
        this.table.setShowRoot(false);
        this.table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        this.table.getColumns().addAll(dueDateCol, inProgressCol, assignedUserFieldCol, typeCol, salesOrderCol, storeCodeCol, workOrderCol, partIDCol, descriptionCol, quantityCol, labourHourCol, jobStartDate);

        AnchorPane.setLeftAnchor(this.table, 0.0);
        AnchorPane.setRightAnchor(this.table, 0.0);
        AnchorPane.setTopAnchor(this.table, 0.0);
        AnchorPane.setBottomAnchor(this.table, 0.0);

        tableFiltering();

        this.tableContainer.getChildren().add(this.table);
    }

    private void initializeBottomContainer() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("[HH:mm:ss uuuu-MM-dd]");

        Platform.runLater(() -> this.lastRefreshedLabel.setText("UI Last Refreshed: " + now.format(df)));

    }

    private void tableFiltering() {
        this.table.getRoot().getChildren().clear();
        List<TreeItem<WorkOrder>> temp = FXCollections.observableArrayList();
        for (WorkOrder workOrder : this.allWorkOrders) {
            String type = workOrder.getType().getValue();

            boolean isSeonSelected = this.seonCheckbox.isSelected() && type.equals("PT");
            boolean isCobanSelected = this.cobanCheckbox.isSelected() && type.equals("LAW");
            boolean isFleetmindSelected = this.fleetmindCheckbox.isSelected() && type.equals("WASTE");
            boolean isItSelected = this.itCheckbox.isSelected() && type.equals("IT PROD");

            if (isSeonSelected || isCobanSelected || isFleetmindSelected || isItSelected) {
                temp.add(new TreeItem<>(workOrder));
            }
        }

        this.table.getRoot().getChildren().addAll(temp);

    }

    public void onExportData() {
        try {
            new ExportScene(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onRefreshTime() {
        try {
            new RefreshSettingScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAbout() {
        Messages.displayInfoMessage("About SFTaskFlow",
                "SFTaskFlow\n" +
                        "Build #2.5 SNAPSHOT, built on July 5th, 2024\n" +
                        "VM: JDK 17.0.11\" 2024-04-16 LTS\n" +
                        "Windows 10.0\n" +
                        "Created By: Mike Nguyen | github.com/Pandaism\n");
    }

    public TreeTableView<WorkOrder> getTable() {
        return table;
    }
}