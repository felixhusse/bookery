/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background.thumbnail;

import com.google.gson.Gson;
import de.fatalix.bookery.bl.background.BatchJobInterface;
import de.fatalix.bookery.bl.dao.BatchJobConfigurationDAO;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.inject.Inject;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author felix.husse
 */
@Stateless
public class ThumbnailBatch implements BatchJobInterface{
    
    @Inject private SolrHandler solrHandler;
    @Inject private BatchJobConfigurationDAO dao;
    
    @Inject private Logger logger;
    
    @Override
    public void executeJob(Timer timer) {
        try {
            BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
            Gson gson = new Gson();
            ThumbnailBatchConfiguration config = gson.fromJson(jobConfig.getConfigurationXML(), ThumbnailBatchConfiguration.class);
            QueryResponse response = solrHandler.searchSolrIndex("-thumbnailgenerated:done", "id,author,title,thumbnailgenerated,thumbnail,cover", config.getBatchSize(), 0);
            List<BookEntry> bookeEntries = response.getBeans(BookEntry.class);
            
            List<SolrInputDocument> solrDocs = new ArrayList<>();
            for (BookEntry bookEntry : bookeEntries) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                        if (bookEntry.getCover()!= null) {
                            Thumbnails.of(new ByteArrayInputStream(bookEntry.getCover()))
                                .size(config.getWidth(), config.getHeight())
                                .toOutputStream(output);
                            
                            SolrInputDocument doc = new SolrInputDocument();
                            doc.addField("id", bookEntry.getId());

                            Map<String, Object> thumbnailData = new HashMap<>();
                            thumbnailData.put("set", output.toByteArray());
                            doc.addField("thumbnail", thumbnailData);

                            Map<String, Object> thumbnailStatus = new HashMap<>();
                            thumbnailStatus.put("set", "done");
                            doc.addField("thumbnailgenerated", thumbnailStatus);
                            solrDocs.add(doc);    
                        }
                        
                    } catch(IOException ex) {
                        logger.error(ex, ex);
                    }
            }
            long totalLeft = response.getResults().getNumFound();
            if (totalLeft > 0) {
                if (totalLeft < config.getBatchSize()) {
                    totalLeft = 0;
                }
                else {
                    totalLeft = totalLeft - config.getBatchSize();
                }
                jobConfig.setStatus("Total left: " + totalLeft);
                dao.update(jobConfig);
            }
            
            
            solrHandler.updateDocument(solrDocs);
        } catch(SolrServerException | IOException ex) {
            logger.error(ex, ex);
        }
    }
    
}
