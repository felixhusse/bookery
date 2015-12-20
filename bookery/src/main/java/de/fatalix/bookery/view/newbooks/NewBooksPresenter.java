/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.newbooks;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.TimeRange;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author felixhusse1
 */
@UIScoped
public class NewBooksPresenter {
    
    @Inject private BookService bookService;
    @Inject private AppUserService userService;
    
    public QueryResponse searchBooks(String search,int rows,int startOffset, String viewer) throws SolrServerException, IOException {
        QueryResponse response = bookService.newBooksSearch(search,rows,startOffset,viewer);
        bookService.updateViewed(response.getBeans(BookEntry.class),viewer);
        return response;
    }
}
