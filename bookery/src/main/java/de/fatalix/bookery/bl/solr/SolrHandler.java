/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.solr;

import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.model.SettingKey;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author felix.husse
 */
@Stateless
public class SolrHandler {
    @Inject private AppSettingDAO settingDAO;
    
    public void addBeans(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        solr.addBeans(bookEntries);
        UpdateResponse response = solr.commit(true, false);
    }
    
    public void updateBean(BookEntry bookEntry) throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        solr.addBean(bookEntry);
        solr.commit();
        
    }
    
    public void updateDocument(SolrInputDocument document) throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        solr.add(document);
        solr.commit();
    }
    
    public void resetSolrIndex() throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        solr.deleteByQuery("*:*");
        solr.commit();
    }
    
    public QueryResponse searchSolrIndex(String queryString, String fields, int rows, int startOffset) throws SolrServerException  {
        SolrServer solr = null;
        try {
            solr = createConnection();
        } catch (IOException ex) {
            throw new SolrServerException(ex.getMessage());
        }
        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setRows(rows);
        query.setStart(startOffset);
        
        query.setFields(fields);
        QueryResponse rsp = solr.query(query);
        return rsp;
    }
    
    public List<BookEntry> getBookDetail(String bookID) throws SolrServerException {
        SolrServer solr = null;
        try {
            solr = createConnection();
        } catch (IOException ex) {
            throw new SolrServerException(ex.getMessage());
        }
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+bookID);
        query.setRows(1);
        query.setFields("id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,reader,shared,cover,thumbnail,thumbnailgenerated");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
    
    public List<BookEntry> getBookData(String bookID) throws SolrServerException {
        SolrServer solr = null;
        try {
            solr = createConnection();
        } catch (IOException ex) {
            throw new SolrServerException(ex.getMessage());
        }
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+bookID);
        query.setRows(1);
        query.setFields("id,file");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
    
    public long checkSolr() throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(1);
        QueryResponse rsp = solr.query(query);
        
        return rsp.getResults().getNumFound();
    }
    
    private SolrServer createConnection() throws SolrServerException, IOException {
        String solrURL = settingDAO.findByKey(SettingKey.SOLR_URL).getConfigurationValue();
        String solrCore = settingDAO.findByKey(SettingKey.SOLR_CORE).getConfigurationValue();
        
        if (!solrURL.endsWith("/")) {
            solrURL = solrURL + "/";
        }
        HttpSolrServer solrServer = new HttpSolrServer(solrURL + solrCore);
        try {
            
            if (solrServer.ping().getStatus()!=0) {
                throw new SolrServerException("Solr Server not found! ");
            }    
        } catch (HttpSolrServer.RemoteSolrException ex) {
            throw new SolrServerException(ex.getMessage(),ex);
        }
        
        return solrServer;
    }
}
