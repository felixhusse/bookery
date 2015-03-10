/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Singleton;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

/**
 *
 * @author felix.husse
 */
@Stateless
public class SolrHandler {


    
    public void addBeans(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.3/bookery");
        solr.addBeans(bookEntries);
        UpdateResponse response = solr.commit();
    }
    
    public void resetSolrIndex() throws SolrServerException, IOException {
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.3/bookery");
        solr.deleteByQuery("*:*");
    }
    
    
}
