/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

/**
 *
 * @author felix.husse
 */
public enum TimeRange {
    
    NONE("none"),SINCELASTLOGIN("last login"),LASTWEEK("last week"),LASTMONTH("last month");
    
    private final String caption;
    
    private TimeRange(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
    
    
}
