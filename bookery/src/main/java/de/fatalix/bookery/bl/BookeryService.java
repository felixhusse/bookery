/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.background.BatchJobService;
import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import de.fatalix.bookery.bl.model.SettingKey;
import java.io.IOException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Basic Service for initializing and keeping 
 * @author felix.husse
 */
@ApplicationScoped
public class BookeryService {
    @Inject private BatchJobService batchJobService;
    @Inject private AppSettingDAO settingDAO;
    private SolrServer solrServer;
    
    public void fireUpBatchJobs() {
        List<BatchJobConfiguration> batchJobs = batchJobService.getAllJobs();
        for (BatchJobConfiguration batchJob : batchJobs) {
            batchJobService.fireUpTimer(batchJob);
        }
    }
    
    public void updateConfiguration() throws SolrServerException, IOException {
        String solrURL = settingDAO.findByKey(SettingKey.SOLR_URL).getConfigurationValue();
        String solrCore = settingDAO.findByKey(SettingKey.SOLR_CORE).getConfigurationValue();
        
        if (!solrURL.endsWith("/")) {
            solrURL = solrURL + "/";
        }
        solrServer = new HttpSolrServer(solrURL + solrCore);
        try {
            
            if (solrServer.ping().getStatus()!=0) {
                throw new SolrServerException("Solr Server not found! ");
            }    
        } catch (HttpSolrServer.RemoteSolrException ex) {
            throw new SolrServerException(ex.getMessage(),ex);
        }
    }
    
    public SolrServer getSolrConnection() throws SolrServerException, IOException {
        if (solrServer == null) {
            updateConfiguration();
        }
        return solrServer;
    }
    
}
