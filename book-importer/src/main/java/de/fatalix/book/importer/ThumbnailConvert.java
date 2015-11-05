/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author felix.husse
 */
public class ThumbnailConvert {
    
    public static void createThumbnails(String solrURL, String solrCore, int batchSize) throws SolrServerException, IOException {
        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        convertBatchWise(server, batchSize, 0);
    }
    
    public static void createThumbnailForBook(String solrURL, String solrCore, String bookID) throws SolrServerException, IOException {
        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        QueryResponse response = SolrHandler.searchSolrIndex(server, "id:"+bookID, 1, 0);
        List<BookEntry> bookEntries = response.getBeans(BookEntry.class);
        System.out.println("Retrieved " + (bookEntries.size()) + " of " + response.getResults().getNumFound());
        for(BookEntry bookEntry : bookEntries) {
            if (bookEntry.getCover() != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(bookEntry.getCover()))
                                    .size(130, 200)
                                    .toOutputStream(output);

                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", bookEntry.getId());
                
                Map<String, Object> thumbnailData = new HashMap<>();
                thumbnailData.put("set", output.toByteArray());
                doc.addField("thumbnail", thumbnailData);
                
                Map<String, Object> thumbnailStatus = new HashMap<>();
                thumbnailStatus.put("set", "done");
                doc.addField("thumbnailgenerated", thumbnailStatus);
                
                server.add(doc);
                server.commit();    
            }
             
        }
    }
    
    private static void convertBatchWise(SolrServer server,int batchSize, int offset) throws SolrServerException, IOException {
        QueryResponse response = SolrHandler.searchSolrIndex(server, "-thumbnailgenerated:done", batchSize, offset);
        List<BookEntry> bookEntries = response.getBeans(BookEntry.class);
        System.out.println("Retrieved " + (bookEntries.size() + offset) + " of " + response.getResults().getNumFound());
        for(BookEntry bookEntry : bookEntries) {
            if (bookEntry.getCover() != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Thumbnails.of(new ByteArrayInputStream(bookEntry.getCover()))
                                    .size(130, 200)
                                    .toOutputStream(output);

                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", bookEntry.getId());
                
                Map<String, Object> thumbnailData = new HashMap<>();
                thumbnailData.put("set", output.toByteArray());
                doc.addField("thumbnail", thumbnailData);
                
                Map<String, Object> thumbnailStatus = new HashMap<>();
                thumbnailStatus.put("set", "done");
                doc.addField("thumbnailgenerated", thumbnailStatus);
                
                server.add(doc);
                server.commit();     
            }
             
        }
        response = SolrHandler.searchSolrIndex(server, "-thumbnailgenerated:done", batchSize, offset);
        if(response.getResults().getNumFound() > 0) {
            convertBatchWise(server, batchSize, 0);
        }
    }
}
