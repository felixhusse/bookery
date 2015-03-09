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
    
    JIRA_URL("bookery.elasticsearch.url","Elasticsearch","http://jira.medavis.local"),
    EL_INDEX("bookery.elasticsearch.index","Elasticsearch Index","bookery"),
    EL_TYPE("bookery.elasticsearch.type","Elasticsearch Type","book"),
    JIRA_PASS("calculator.jira.password","JIRA Password","");
    
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
