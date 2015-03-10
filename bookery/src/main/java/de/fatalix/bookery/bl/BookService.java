/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import de.fatalix.bookery.bl.elasticsearch.SolrHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BookService {

    @Inject private SolrHandler solrHandler;
    
    public void addBooks(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solrHandler.addBeans(bookEntries);
    }
 
    
    public List<BookEntry> searchBooks(String searchword) {
        
        return Collections.EMPTY_LIST;
    }
}
