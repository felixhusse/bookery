/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.IOException;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class BookDetailPresenter {
    @Inject private BookService bookService;
    @Inject private AppUserService userService;
    
    
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
