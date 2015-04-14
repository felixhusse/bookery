/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
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
