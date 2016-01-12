/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.model;


import de.fatalix.bookery.bl.EntityIntf;
import de.fatalix.bookery.bl.background.BatchJobType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author felix.husse
 */
@Entity
public class BatchJobConfiguration implements EntityIntf, Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    @Enumerated(EnumType.STRING)
    private BatchJobType type;
    private String cronJobExpression;
    private String configurationXML;
    private String status;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date nextTimeout;
    
    private boolean active;
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public BatchJobType getType() {
        return type;
    }

    public void setType(BatchJobType type) {
        this.type = type;
    }

    public String getCronJobExpression() {
        return cronJobExpression;
    }

    // SEC-MIN-HOUR-DAY-MONTH-YEAR-DAYOfWeek
    public void setCronJobExpression(String cronJobExpression) {
        this.cronJobExpression = cronJobExpression;
    }
    

    public String getConfigurationXML() {
        return configurationXML;
    }

    public void setConfigurationXML(String configurationXML) {
        this.configurationXML = configurationXML;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getNextTimeout() {
        return nextTimeout;
    }

    public void setNextTimeout(Date nextTimeout) {
        this.nextTimeout = nextTimeout;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
}
