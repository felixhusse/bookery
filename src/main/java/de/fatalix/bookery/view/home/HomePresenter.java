/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class HomePresenter {
    @Inject private BookService service;
    
    public boolean addBook(BookEntry book) {
        return service.addBook(book);
    }
    
    public List<BookEntry> searchBooks(String search) {
        return service.searchBooks(search);
    }
}
