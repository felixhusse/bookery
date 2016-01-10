/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.TimeRange;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class BookSearchPresenter {
    
    @Inject private BookService bookService;
    
    public QueryResponse searchBooks(SolrQuery solrQuery) throws SolrServerException {
        return bookService.searchBooks(solrQuery);
    }
    
    public QueryResponse searchBooks(String search,int rows,int startOffset,String viewer) throws SolrServerException, IOException {
        QueryResponse response = bookService.searchBooks(search,rows,startOffset,TimeRange.NONE);        
        return response;
    }
    
    public QueryResponse searchNewBooks(String search,int rows,int startOffset, String viewer) throws SolrServerException, IOException {
        QueryResponse response = bookService.newBooksSearch(search,rows,startOffset,viewer);
        return response;
    }
    
    public void updateViewed(List<BookEntry> bookEntries, String viewer) throws SolrServerException, IOException {
        bookService.updateViewed(bookEntries,viewer);
    }
}
