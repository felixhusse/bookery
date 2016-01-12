/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.WatchListService;
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
    @Inject private WatchListService watchListService;

    public long getBookCount() throws SolrServerException {
        return bookService.getTotalCount();
    }
    
    public QueryResponse searchBooks(SolrQuery solrQuery) throws SolrServerException {
        return bookService.searchBooks(solrQuery);
    }
    
    public SolrQuery getWatchListQuery(String username) {
        return watchListService.getSolrQuery(watchListService.getUserWatchList(username));
    }
    
}
