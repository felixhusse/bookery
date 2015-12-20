/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.model;

/**
 *
 * @author felix.husse
 */
public enum SettingKey {
    
    SOLR_URL("bookery.solr.url","Solr URL","http://localhost:8080/solr-4.10.4"),
    SOLR_CORE("bookery.solr.core","Solr Core","bookery"),
    CALIBRE_PATH("calibre.path","Calibre Path","/opt/calibre"),
    CALIBRE_WORK("calibre.work","Calibre Work","/home/bookconvert");
    
    private final String key;
    private final String defaultValue;
    private final String name;

    private SettingKey(String key, String name, String defaultValue) {
        this.key = key;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }
    
    public static SettingKey getEnumByKey(String key) {
        for (SettingKey settingKey : values()) {
            if (settingKey.getKey().equals(key)) {
                return settingKey;
            }
        }
        return null;
    }
}
