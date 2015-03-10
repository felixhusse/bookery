/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.fileimport;

import java.util.concurrent.Executor;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

/**
 *
 * @author felix.husse
 */
@Stateless
public class TransactionalExecutor implements Executor{

    @Override 
    @Asynchronous
    public void execute(Runnable command) {
        command.run();
    }
    
}
