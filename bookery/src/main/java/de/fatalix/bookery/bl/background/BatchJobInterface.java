/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
