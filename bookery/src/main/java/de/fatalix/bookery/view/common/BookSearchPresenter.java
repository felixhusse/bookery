/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
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
    
    public QueryResponse searchBooks(SolrQuery solrQuery, String viewer) throws SolrServerException {
        QueryResponse response = bookService.searchBooks(solrQuery);
        bookService.updateViewed(response.getBeans(BookEntry.class),viewer);
        return response;
    }

    public void updateViewed(List<BookEntry> bookEntries, String viewer) throws SolrServerException, IOException {
        bookService.updateViewed(bookEntries,viewer);
    }
}
