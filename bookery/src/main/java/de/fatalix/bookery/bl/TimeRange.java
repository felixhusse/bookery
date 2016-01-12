/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
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
