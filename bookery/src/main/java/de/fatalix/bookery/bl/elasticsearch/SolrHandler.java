/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

import java.io.IOException;
import java.util.List;
import javax.ejb.Stateless;
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


    
    public void addBeans(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.4/bookery");
        solr.addBeans(bookEntries);
        UpdateResponse response = solr.commit();
    }
    
    public void resetSolrIndex() throws SolrServerException, IOException {
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.4/bookery");
        solr.deleteByQuery("*:*");
    }
    
    public List<BookEntry> searchSolrIndex(String searchWord) throws SolrServerException  {
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.4/bookery");
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
        SolrServer solr = new HttpSolrServer("http://localhost:8080/solr-4.10.4/bookery");
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+bookID);
        query.setRows(1);
        query.setFields("id,author,title,isbn,publisher,description,language,releaseDate,rating,uploader,reader,shared,cover");
        QueryResponse rsp = solr.query(query);
        return rsp.getBeans(BookEntry.class);
    }
}
