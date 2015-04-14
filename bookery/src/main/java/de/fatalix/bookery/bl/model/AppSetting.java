/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.model;

import de.fatalix.bookery.bl.EntityIntf;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author felix.husse
 */
@Entity
@NamedQueries({
    @NamedQuery(name=AppSetting.FIND_BY_KEY, query="SELECT s FROM AppSetting s WHERE s.configurationKey =:key"),
})
public class AppSetting implements EntityIntf, Serializable{
    public static final String FIND_BY_KEY = "Calculator.findByKey";
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    private String configurationKey;
    private String configurationValue;
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public void setConfigurationValue(String configurationValue) {
        this.configurationValue = configurationValue;
    }
}
