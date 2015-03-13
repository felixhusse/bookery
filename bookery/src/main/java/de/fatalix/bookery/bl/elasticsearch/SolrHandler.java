/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

import de.fatalix.bookery.bl.dao.AppSettingDAO;
import de.fatalix.bookery.bl.model.SettingKey;
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
        UpdateResponse response = solr.commit();
    }
    
    public void resetSolrIndex() throws SolrServerException, IOException {
        SolrServer solr = createConnection();
        solr.deleteByQuery("*:*");
    }
    
    public List<BookEntry> searchSolrIndex(String searchWord) throws SolrServerException  {
        SolrServer solr = createConnection();
        SolrQuery query = new SolrQuery();
        if (searchWord != null && !searchWord.isEmpty()) {
            query.setQuery("author:*"+searchWord + "* OR title:*"+searchWord+"*");
        }
        else {
            query.setQuery("*:*");
        }
        query.setRows(68);
        query.setFields("id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
    
    public List<BookEntry> getBookDetail(String bookID) throws SolrServerException {
        SolrServer solr = createConnection();
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+bookID);
        query.setRows(1);
        query.setFields("id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,reader,shared,cover");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
    
    public List<BookEntry> getBookData(String bookID) throws SolrServerException {
        SolrServer solr = createConnection();
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+bookID);
        query.setRows(1);
        query.setFields("id,file");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
    
    private SolrServer createConnection() {
        String solrURL = settingDAO.findByKey(SettingKey.SOLR_URL).getConfigurationValue();
        String solrCore = settingDAO.findByKey(SettingKey.SOLR_CORE).getConfigurationValue();
        
        if (!solrURL.endsWith("/")) {
            solrURL = solrURL + "/";
        }

        return new HttpSolrServer(solrURL + solrCore);
    }
}
