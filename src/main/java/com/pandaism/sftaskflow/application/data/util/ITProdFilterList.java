package com.pandaism.sftaskflow.application.data.util;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ITProdFilterList {
    private final List<String> filterList;
    private final File filterFile = new File("V:\\Non-Critical\\Production\\Tools\\libs\\SFTaskFlow\\ITProdFilters.txt");

    public ITProdFilterList() {
        this.filterList = new ArrayList<>();
    }

    public Task<Void> refresh() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                filterList.clear();

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(filterFile));
                    String line;
                    while((line = reader.readLine()) != null) {
                        filterList.add(line);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
    }

    public List<String> getFilterList() {
        return filterList;
    }
}
