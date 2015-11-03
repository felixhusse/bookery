/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.background;

/**
 *
 * @author felix.husse
 */
public enum BatchJobType {
    THUMBNAIL("java:module/ThumbnailJob","Thumbnail Job","converts thumbnails","");
    
    private final String moduleName;
    private final String displayName;
    private final String description;
    private final String defaultConfig;

    private BatchJobType(String moduleName,String displayName,String description, String defaultConfig) {
        this.moduleName = moduleName;
        this.displayName = displayName;
        this.description = description;
        this.defaultConfig = defaultConfig;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }
    
    public static BatchJobType fromDisplayName(String displayName) {
        for (BatchJobType type : values()) {
            if (displayName.equals(type.getDisplayName())) {
                return type;
            }
        }
        return null;
    }
}
