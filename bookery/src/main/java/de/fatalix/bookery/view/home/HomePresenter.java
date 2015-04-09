/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.solr.BookEntry;
import de.fatalix.bookery.bl.model.AppUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class HomePresenter {
    @Inject private BookService bookService;
    @Inject private AppUserService userService;

    public void addBook(BookEntry book) throws SolrServerException, IOException {
        List<BookEntry> bookEntries = new ArrayList<>();
        bookEntries.add(book);
        bookService.addBooks(bookEntries);
    }
    
    public QueryResponse searchBooks(String search,int rows,int startOffset) throws SolrServerException {
        
        return bookService.searchBooks(search,rows,startOffset);
    }
    
    public BookEntry getBookDetail(String id) throws SolrServerException {
        return bookService.getBookDetail(id);
    }
    
    public void shareBookWithKindle(String bookId, String username) throws SolrServerException, MessagingException {
        AppUser appUser = userService.getAppUser(username);
        bookService.sendBookToKindle(bookId, appUser);
    }
}
