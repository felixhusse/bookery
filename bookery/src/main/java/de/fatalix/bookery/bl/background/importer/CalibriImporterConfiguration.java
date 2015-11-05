/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background.importer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author felix.husse
 */
public class CalibriImporterConfiguration {
    
    private String importFolder;
    private String errorFolder;
    private int batchSize;

    public String getImportFolder() {
        return importFolder;
    }

    public void setImportFolder(String importFolder) {
        this.importFolder = importFolder;
    }

    public String getErrorFolder() {
        return errorFolder;
    }

    public void setErrorFolder(String errorFolder) {
        this.errorFolder = errorFolder;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    
    
    public static String generateDefaultConfig() {
        CalibriImporterConfiguration defaultConfig = new CalibriImporterConfiguration();
        defaultConfig.setErrorFolder("calibri/error");
        defaultConfig.setImportFolder("calibri/import");
        defaultConfig.setBatchSize(20);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(defaultConfig);
    }
    
}
