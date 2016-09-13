/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.book.importer;

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
    private String solrURL;
    private String solrCore;

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

    public String getSolrURL() {
        return solrURL;
    }

    public void setSolrURL(String solrURL) {
        this.solrURL = solrURL;
    }

    public String getSolrCore() {
        return solrCore;
    }

    public void setSolrCore(String solrCore) {
        this.solrCore = solrCore;
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
