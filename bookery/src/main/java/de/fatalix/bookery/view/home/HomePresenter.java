/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class HomePresenter {
    @Inject private BookService service;
    
    public void addBook(BookEntry book) throws SolrServerException, IOException {
        List<BookEntry> bookEntries = new ArrayList<>();
        bookEntries.add(book);
        service.addBooks(bookEntries);
    }
    
    public List<BookEntry> searchBooks(String search) throws SolrServerException {
        
        return service.searchBooks(search);
    }
    
    public BookEntry getBookDetail(String id) throws SolrServerException {
        return service.getBookDetail(id);
    }
}
