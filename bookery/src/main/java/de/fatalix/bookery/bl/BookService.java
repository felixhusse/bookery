/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.solr.BookEntry;
import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.bl.model.AppUser;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BookService {

    @Inject private SolrHandler solrHandler;
    @Inject private BookeryMailService mailService;
    
    public void addBooks(List<BookEntry> bookEntries) throws SolrServerException, IOException {
        solrHandler.addBeans(bookEntries);
    }
 
    
    public List<BookEntry> searchBooks(String searchword) throws SolrServerException {
        return solrHandler.searchSolrIndex(searchword);
    }
    
    public BookEntry getBookDetail(String id) throws SolrServerException {
        return solrHandler.getBookDetail(id).get(0);
    }
    
    public void sendBookToKindle(String bookId, AppUser user) throws SolrServerException, MessagingException {
        BookEntry bookFileName = solrHandler.getBookDetail(bookId).get(0);
        String filename = bookFileName.getTitle() + "-" +bookFileName.getAuthor();
        BookEntry bookEntry = solrHandler.getBookData(bookId).get(0);
        mailService.sendKindleMail(user, bookEntry.getFile(),filename);
    }
}
