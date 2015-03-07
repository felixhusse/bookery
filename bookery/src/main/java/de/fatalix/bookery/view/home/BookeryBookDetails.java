/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import javax.annotation.PostConstruct;

/**
 *
 * @author Fatalix
 */
public class BookeryBookDetails extends CustomComponent{
    
    private TextField authorField;
    private TextField titleField;
    private TextField categoryField;
    private TextField isbnField;
    
    @PostConstruct
    private void postInit() {
        authorField = new TextField("Author","");
        titleField = new TextField("Title", "");
        categoryField = new TextField("Category", "eBook");
        isbnField = new TextField("ISBN", "");
        
        FormLayout bookDetailsForm = new FormLayout(authorField,titleField,categoryField,isbnField);
        bookDetailsForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        bookDetailsForm.setMargin(true);
        
        this.setCompositionRoot(bookDetailsForm);
    }
    
    public void changeBookData(BookEntry bookEntry) {
        if (bookEntry.getAuthor() != null || !bookEntry.getAuthor().isEmpty()) {
            authorField.setValue(bookEntry.getAuthor());
        }
        if (bookEntry.getTitle()!= null || !bookEntry.getTitle().isEmpty()) {
            titleField.setValue(bookEntry.getTitle());
        }
        
    }
    
    public BookEntry getBookData() {
        
        return new BookEntry().setAuthor(authorField.getValue())
                .setTitle(titleField.getValue())
                .setCategory(categoryField.getValue())
                .setIsbn(isbnField.getValue());
        
    }
    
}
