/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background.thumbnail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author felix.husse
 */
public class ThumbnailBatchConfiguration {
    private int batchSize;
    private int width;
    private int height;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    
    public static String generateDefaultConfig() {
        ThumbnailBatchConfiguration defaultConfig = new ThumbnailBatchConfiguration();
        defaultConfig.setBatchSize(20);
        defaultConfig.setWidth(130);
        defaultConfig.setHeight(200);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(defaultConfig);
    }
    
}
