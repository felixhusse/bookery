/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.search;

import de.fatalix.bookery.view.home.*;
import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.TimeRange;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class SearchPresenter {
    @Inject private BookService bookService;
    @Inject private AppUserService userService;

    public void addBook(BookEntry book) throws SolrServerException, IOException {
        List<BookEntry> bookEntries = new ArrayList<>();
        bookEntries.add(book);
        bookService.addBooks(bookEntries);
    }
    
    public QueryResponse searchBooks(String search,int rows,int startOffset, TimeRange timeRange,String viewer) throws SolrServerException, IOException {
        QueryResponse response = bookService.searchBooks(search,rows,startOffset,timeRange);
        bookService.updateViewed(response.getBeans(BookEntry.class),viewer);
        return response;
    }
    
}
