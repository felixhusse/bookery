/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class HomePresenter {
    @Inject private BookService bookService;

    public long getBookCount() throws SolrServerException {
        return bookService.getTotalCount();
    }
    
    public QueryResponse searchBooks(SolrQuery solrQuery) throws SolrServerException {
        return bookService.searchBooks(solrQuery);
    }
    
    
}
