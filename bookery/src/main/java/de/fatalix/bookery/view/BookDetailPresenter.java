/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.TimeRange;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class BookDetailPresenter {
    @Inject private BookService bookService;
    @Inject private AppUserService userService;
    
    public QueryResponse searchBooks(String search,int rows,int startOffset, TimeRange timeRange,String viewer) throws SolrServerException, IOException {
        QueryResponse response = bookService.searchBooks(search,rows,startOffset,timeRange);
        bookService.updateViewed(response.getBeans(BookEntry.class),viewer);
        return response;
    }
    
    
    public BookEntry getBookDetail(String id) throws SolrServerException {
        return bookService.getBookDetail(id);
    }
    
    public void shareBookWithKindle(BookEntry bookEntry, String username) throws SolrServerException, MessagingException, IOException {
        AppUser appUser = userService.getAppUser(username);
        bookService.sendBookToKindle(bookEntry.getId(), appUser);
    }
    
    public byte[] getEbookFile(String bookId) throws SolrServerException {
        
        return bookService.getEBookFile(bookId);
    }
    
    public BookEntry updateShared(BookEntry bookEntry, String username) throws SolrServerException, IOException {
        return bookService.updateShared(bookEntry, username);
    }
    
    public BookEntry updateLike(BookEntry bookEntry,String username) throws SolrServerException, IOException {
        return bookService.updateLike(bookEntry, username);
    }
}
