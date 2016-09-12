/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.WatchListService;
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
    @Inject private WatchListService watchListService;
    
    public QueryResponse searchBooks(SolrQuery solrQuery, String viewer) throws SolrServerException {
        QueryResponse response = bookService.searchBooks(solrQuery);
        bookService.updateViewed(response.getBeans(BookEntry.class),viewer);
        return response;
    }

    public void updateViewed(List<BookEntry> bookEntries, String viewer) throws SolrServerException, IOException {
        bookService.updateViewed(bookEntries,viewer);
    }
    
    public BookEntry updateLike(BookEntry bookEntry,String username) throws SolrServerException, IOException {
        return bookService.updateLike(bookEntry, username);
    }
    
    public boolean addRemoveFromWatchList(BookEntry bookEntry, String username) {
        
        if (!isOnWatchList(bookEntry, username)) {
            watchListService.addToWatchList(username, bookEntry.getId());
            return true;
        }
        else {
            watchListService.removeFromWatchList(username, bookEntry.getId());
            return false;
        }
    }
    
    public boolean isOnWatchList(BookEntry bookEntry, String username) {
        return watchListService.isOnWatchList(username, bookEntry.getId());  
    }
}
