/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background;

import javax.ejb.Local;
import javax.ejb.Timer;

/**
 *
 * @author felix.husse
 */
@Local
public interface BatchJobInterface {
    public void executeJob(Timer timer);

}
