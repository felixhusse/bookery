/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import java.util.List;
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
public class SolrHandler {
    public static SolrServer createConnection(String solrURL, String solrCore) throws SolrServerException, IOException {
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
    
    public static QueryResponse searchSolrIndex(SolrServer solr,String queryString, int rows, int startOffset) throws SolrServerException  {
        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setRows(rows);
        query.setStart(startOffset);
        QueryResponse rsp = solr.query(query);
        return rsp;
    }
    
    public static UpdateResponse addBeans(SolrServer solr, List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solr.addBeans(bookEntries);
        return solr.commit();
    }
    
    public static void resetSolrIndex(SolrServer solr) throws SolrServerException, IOException {
        solr.deleteByQuery("*:*");
        solr.commit();
    }
    
}
