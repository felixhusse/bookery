/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background;

import de.fatalix.bookery.bl.dao.BatchJobConfigurationDAO;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BatchJobService {

    @Resource
    private TimerService timerService;	//Resource Injection
    
    @Inject private Logger logger;
    

    @Inject private BatchJobConfigurationDAO batchJobConfigurationDAO;

    @Timeout
    public void timeout(Timer timer) {
        try {
            logger.debug("Executing Timer ");
            BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
            logger.debug("Executing Batch Job " + jobConfig.getType().getDisplayName());
            BatchJobInterface batchJob = (BatchJobInterface)InitialContext.doLookup(jobConfig.getType().getModuleName());
            
            batchJob.executeJob(timer); //Asynchronous method
        } catch(NamingException ex) {
            logger.error(ex,ex);
        }
    }
    
    public BatchJobConfiguration saveJob(BatchJobConfiguration batchJobConfiguration) {
        BatchJobConfiguration result =  batchJobConfigurationDAO.save(batchJobConfiguration);
        result = fireUpTimer(result);
        return result;
    }
    
    public BatchJobConfiguration updateJob(BatchJobConfiguration batchJobConfiguration) {
        Timer timer = getTimer(batchJobConfiguration);
        if (timer != null) {
            BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
            logger.debug("Canceling Timer " + jobConfig.getType().getDisplayName());
            timer.cancel();
        }
        BatchJobConfiguration result = batchJobConfigurationDAO.update(batchJobConfiguration);
        result = fireUpTimer(result);
        return result;
    }
    
    public void deleteJob(BatchJobConfiguration batchJobConfiguration) {
        Timer timer = getTimer(batchJobConfiguration);
        if (timer != null) {
            BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
            logger.debug("Delete Timer " + jobConfig.getType().getDisplayName());
            timer.cancel();
        }
        batchJobConfigurationDAO.delete(batchJobConfiguration.getId());
    }
    
    public void cancelJob(BatchJobConfiguration batchJobConfiguration) {
        Timer timer = getTimer(batchJobConfiguration);
        if (timer != null) {
            BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
            logger.debug("Canceling Timer " + jobConfig.getType().getDisplayName());
            timer.cancel();
        }
        batchJobConfiguration.setActive(false);
        batchJobConfigurationDAO.update(batchJobConfiguration);
    }
    
    public List<BatchJobConfiguration> getAllJobs() {
        List<BatchJobConfiguration> jobConfigs = batchJobConfigurationDAO.findAll();
        for (BatchJobConfiguration config : jobConfigs) {
            Timer timer = getTimer(config);
            if (timer!=null) {
                config.setNextTimeout(timer.getNextTimeout());
                config = batchJobConfigurationDAO.update(config);
            }
        }
        return jobConfigs;
    }
    
    public BatchJobConfiguration fireUpTimer(BatchJobConfiguration jobConfig) {
        if (jobConfig.isActive()) {
            logger.debug("Configured batch job " + jobConfig.getType().getDisplayName());
            TimerConfig timerConf = new TimerConfig(jobConfig, false);
            String[] splittedCronJob = jobConfig.getCronJobExpression().split(" ");
            ScheduleExpression schedExp = new ScheduleExpression();
            schedExp.second(splittedCronJob[0]);
            schedExp.minute(splittedCronJob[1]);
            schedExp.hour(splittedCronJob[2]);
            schedExp.dayOfMonth(splittedCronJob[3]);
            schedExp.month(splittedCronJob[4]);
            schedExp.year(splittedCronJob[5]);
            schedExp.dayOfWeek(splittedCronJob[6]);
            Timer timer = timerService.createCalendarTimer(schedExp, timerConf);
            jobConfig.setNextTimeout(timer.getNextTimeout());
            jobConfig = batchJobConfigurationDAO.update(jobConfig);
        }
        return jobConfig;
    }
    
    private Timer getTimer(BatchJobConfiguration jobConfig) {
        Collection<Timer> timers = timerService.getTimers();
        for(Timer timer : timers) {
            BatchJobConfiguration config = (BatchJobConfiguration)timer.getInfo();
            if (Objects.equals(config.getId(), jobConfig.getId())) {
                return timer;
            }
        }
        return null;
    }
    
    public Collection<Timer> getAllTimer() {
        return timerService.getTimers();
    }
}
