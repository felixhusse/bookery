/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.WatchListService;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class BookDetailPresenter {
    @Inject private Logger logger;
    @Inject private BookService bookService;
    @Inject private AppUserService userService;
    @Inject private WatchListService watchListService;
    
    
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
    
    public void addRemoveFromWatchList(BookEntry bookEntry, String username) {
        
        if (!isOnWatchList(bookEntry, username)) {
            watchListService.addToWatchList(username, bookEntry.getId());
        }
        else {
            watchListService.removeFromWatchList(username, bookEntry.getId());
        }
    }
    
    public boolean isOnWatchList(BookEntry bookEntry, String username) {
        return watchListService.isOnWatchList(username, bookEntry.getId());  
    }
}
