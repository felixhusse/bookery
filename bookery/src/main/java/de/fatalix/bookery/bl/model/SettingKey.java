/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fatalix.bookery.bl.model;

/**
 *
 * @author felix.husse
 */
public enum SettingKey {
    
    SOLR_URL("bookery.solr.url","Solr URL","http://localhost:8080/solr-4.10.3"),
    SOLR_CORE("bookery.solr.core","Solr Core","bookery");
    
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
